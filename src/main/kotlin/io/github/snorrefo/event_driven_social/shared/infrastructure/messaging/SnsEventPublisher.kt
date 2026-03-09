package io.github.snorrefo.event_driven_social.shared.infrastructure.messaging

import io.github.snorrefo.event_driven_social.shared.infrastructure.config.AwsProperties
import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import io.github.snorrefo.event_driven_social.shared.port.EventPublisher
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.MessageAttributeValue

@Component
class SnsEventPublisher(
    private val snsClient: SnsClient,
    private val awsProperties: AwsProperties
) : EventPublisher {

    override fun publish(entry: OutboxEntry) {
        val topicArn = when (entry.eventType) {
            "post.created" -> awsProperties.sns.postsCreated
            "user.followed" -> awsProperties.sns.usersFollowed
            "post.liked" -> awsProperties.sns.postsLiked
            else -> throw IllegalArgumentException("Unknown event type: ${entry.eventType}")
        }

        snsClient.publish { builder ->
            builder
                .topicArn(topicArn)
                .message(entry.payload)
                .messageAttributes(
                    mapOf(
                        "eventType" to MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue(entry.eventType)
                            .build(),
                        "eventId" to MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue(entry.id.toString())
                            .build()
                    )
                )
        }
    }
}
