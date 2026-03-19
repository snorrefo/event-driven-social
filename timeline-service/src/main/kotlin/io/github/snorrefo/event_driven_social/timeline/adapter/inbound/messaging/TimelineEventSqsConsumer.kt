package io.github.snorrefo.event_driven_social.timeline.adapter.inbound.messaging

import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.shared.event.DomainEvent
import io.github.snorrefo.event_driven_social.shared.event.PostCreatedEvent
import io.github.snorrefo.event_driven_social.shared.event.UserFollowedEvent
import io.github.snorrefo.event_driven_social.shared.event.UserUnfollowedEvent
import io.github.snorrefo.event_driven_social.timeline.application.TimelineApplicationService
import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

@Component
class TimelineEventSqsConsumer(
    private val sqsClient: SqsClient,
    private val objectMapper: ObjectMapper,
    private val timelineApplicationService: TimelineApplicationService,
    @Value("\${app.sqs.timeline-events-queue-url}") private val queueUrl: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 1000)
    fun pollMessages() {
        val request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(10)
            .waitTimeSeconds(5)
            .messageAttributeNames("eventType")
            .build()

        val response = sqsClient.receiveMessage(request)
        response.messages().forEach { message ->
            try {
                val snsWrapper = objectMapper.readTree(message.body())
                @Suppress("DEPRECATION")
                val eventPayload = snsWrapper.get("Message")?.textValue() ?: message.body()
                val event = objectMapper.readValue(eventPayload, DomainEvent::class.java)
                handleEvent(event)

                sqsClient.deleteMessage(
                    DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build()
                )
            } catch (e: Exception) {
                log.error("Failed to process SQS message: ${message.messageId()}", e)
            }
        }
    }

    private fun handleEvent(event: DomainEvent) {
        when (event) {
            is PostCreatedEvent -> {
                log.info("Processing post.created: postId={}", event.postId)
                val entry = TimelineEntry(
                    postId = event.postId,
                    authorId = event.authorId,
                    content = event.content,
                    createdAt = event.occurredAt,
                )
                timelineApplicationService.fanOutPost(entry)
            }
            is UserFollowedEvent -> {
                log.info("Processing user.followed: {} -> {}", event.followerId, event.followedId)
                timelineApplicationService.handleFollow(event.followerId, event.followedId)
            }
            is UserUnfollowedEvent -> {
                log.info("Processing user.unfollowed: {} -> {}", event.followerId, event.followedId)
                timelineApplicationService.handleUnfollow(event.followerId, event.followedId)
            }
        }
    }
}
