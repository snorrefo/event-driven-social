package io.github.snorrefo.event_driven_social.post.adapter.outbound.messaging

import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import io.github.snorrefo.event_driven_social.shared.port.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest

@Component
class SnsEventPublisher(
    private val snsClient: SnsClient,
    @Value("\${app.sns.posts-created-topic-arn}") private val postsCreatedTopicArn: String,
) : EventPublisher {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun publish(entry: OutboxEntry) {
        val topicArn = resolveTopicArn(entry.eventType)
        val request = PublishRequest.builder()
            .topicArn(topicArn)
            .message(entry.payload)
            .messageAttributes(
                mapOf(
                    "eventType" to MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(entry.eventType)
                        .build()
                )
            )
            .build()
        snsClient.publish(request)
        log.info("Published event {} to {}", entry.eventType, topicArn)
    }

    private fun resolveTopicArn(eventType: String): String = when (eventType) {
        "post.created" -> postsCreatedTopicArn
        else -> throw IllegalArgumentException("Unknown event type: $eventType")
    }
}
