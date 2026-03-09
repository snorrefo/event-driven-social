package io.github.snorrefo.event_driven_social.identity.adapter.inbound.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.identity.domain.port.outbound.UserRepository
import io.github.snorrefo.event_driven_social.shared.event.PostCreatedEvent
import io.github.snorrefo.event_driven_social.shared.event.UserFollowedEvent
import io.github.snorrefo.event_driven_social.shared.event.UserUnfollowedEvent
import io.github.snorrefo.event_driven_social.shared.infrastructure.config.AwsProperties
import io.github.snorrefo.event_driven_social.shared.infrastructure.messaging.SqsMessagePoller
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.sqs.SqsClient
import java.util.*

@Component
@ConditionalOnProperty("sqs.polling.enabled", havingValue = "true")
class UserCountSqsConsumer(
    sqsClient: SqsClient,
    awsProperties: AwsProperties,
    objectMapper: ObjectMapper,
    private val userRepository: UserRepository
) : SqsMessagePoller(sqsClient, awsProperties.sqs.userCountEvents, objectMapper) {

    @Scheduled(fixedDelay = 0)
    override fun pollAndProcess() = super.pollAndProcess()

    @Transactional
    override fun handleMessage(eventType: String, payload: String) {
        when (eventType) {
            "post.created" -> {
                val event = objectMapper.readValue(payload, PostCreatedEvent::class.java)
                userRepository.incrementPostsCount(UUID.fromString(event.data.authorId))
            }
            "user.followed" -> {
                val event = objectMapper.readValue(payload, UserFollowedEvent::class.java)
                userRepository.incrementFollowersCount(UUID.fromString(event.data.followedUserId))
                userRepository.incrementFollowingCount(UUID.fromString(event.data.followerId))
            }
            "user.unfollowed" -> {
                val event = objectMapper.readValue(payload, UserUnfollowedEvent::class.java)
                userRepository.decrementFollowersCount(UUID.fromString(event.data.unfollowedUserId))
                userRepository.decrementFollowingCount(UUID.fromString(event.data.followerId))
            }
        }
    }
}
