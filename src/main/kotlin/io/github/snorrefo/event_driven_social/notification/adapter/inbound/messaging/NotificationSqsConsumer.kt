package io.github.snorrefo.event_driven_social.notification.adapter.inbound.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.notification.domain.model.NotificationType
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.CreateNotificationUseCase
import io.github.snorrefo.event_driven_social.shared.event.PostCreatedEvent
import io.github.snorrefo.event_driven_social.shared.event.UserFollowedEvent
import io.github.snorrefo.event_driven_social.shared.infrastructure.config.AwsProperties
import io.github.snorrefo.event_driven_social.shared.infrastructure.messaging.SqsMessagePoller
import io.github.snorrefo.event_driven_social.socialgraph.domain.port.inbound.GetFollowersUseCase
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sqs.SqsClient
import java.util.*

@Component
@ConditionalOnProperty("sqs.polling.enabled", havingValue = "true")
class NotificationSqsConsumer(
    sqsClient: SqsClient,
    awsProperties: AwsProperties,
    objectMapper: ObjectMapper,
    private val createNotificationUseCase: CreateNotificationUseCase,
    private val getFollowersUseCase: GetFollowersUseCase
) : SqsMessagePoller(sqsClient, awsProperties.sqs.notificationEvents, objectMapper) {

    @Scheduled(fixedDelay = 0)
    override fun pollAndProcess() = super.pollAndProcess()

    override fun handleMessage(eventType: String, payload: String) {
        when (eventType) {
            "user.followed" -> {
                val event = objectMapper.readValue(payload, UserFollowedEvent::class.java)
                createNotificationUseCase.createNotification(
                    userId = UUID.fromString(event.data.followedUserId),
                    type = NotificationType.NEW_FOLLOWER,
                    actorId = UUID.fromString(event.data.followerId),
                    referenceId = null
                )
            }
            "post.created" -> {
                val event = objectMapper.readValue(payload, PostCreatedEvent::class.java)
                val authorId = UUID.fromString(event.data.authorId)
                for (followerId in getFollowersUseCase.getFollowerIds(authorId)) {
                    createNotificationUseCase.createNotification(
                        userId = followerId,
                        type = NotificationType.NEW_POST,
                        actorId = authorId,
                        referenceId = UUID.fromString(event.data.postId)
                    )
                }
            }
        }
    }
}
