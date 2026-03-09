package io.github.snorrefo.event_driven_social.events.publisher


import com.fasterxml.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.config.AwsProperties
import io.github.snorrefo.event_driven_social.domain.model.OutboxEvent
import io.github.snorrefo.event_driven_social.domain.repository.OutboxEventRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import java.time.Instant

@Component
class OutboxPublisher(
    private val outboxRepository: OutboxEventRepository,
    private val snsClient: SnsClient,
    private val awsProperties: AwsProperties,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 5000) // Every 5 seconds
    @Transactional
    fun publishPendingEvents() {
        val pendingEvents = outboxRepository
            .findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()

        if (pendingEvents.isEmpty()) {
            return
        }

        logger.info("Publishing ${pendingEvents.size} pending events")

        pendingEvents.forEach { event ->
            try {
                publishEvent(event)

                // Mark as published
                event.publishedAt = Instant.now()
                outboxRepository.save(event)

                logger.info("Published event: ${event.id} (${event.eventType})")
            } catch (e: Exception) {
                logger.error("Failed to publish event: ${event.id}", e)
                // Will retry on next poll
            }
        }
    }

    private fun publishEvent(event: OutboxEvent) {
        val topicArn = when (event.eventType) {
            "post.created" -> awsProperties.sns.postsCreated
            "user.followed" -> awsProperties.sns.usersFollowed
            "post.liked" -> awsProperties.sns.postsLiked
            else -> throw IllegalArgumentException("Unknown event type: ${event.eventType}")
        }

        snsClient.publish { builder ->
            builder
                .topicArn(topicArn)
                .message(event.payload)
                .messageAttributes(
                    mapOf(
                        "eventType" to MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue(event.eventType)
                            .build(),
                        "eventId" to MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue(event.id.toString())
                            .build()
                    )
                )
        }
    }

    // Cleanup old published events (run daily)
    @Scheduled(cron = "0 0 2 * * *") // 2 AM every day
    @Transactional
    fun cleanupOldEvents() {
        val cutoff = Instant.now().minusSeconds(7 * 24 * 60 * 60) // 7 days ago
        val deleted = outboxRepository.deletePublishedBefore(cutoff)
        logger.info("Cleaned up $deleted old outbox events")
    }
}