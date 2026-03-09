package io.github.snorrefo.event_driven_social.identity.adapter.inbound.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.snorrefo.event_driven_social.identity.domain.port.outbound.UserRepository
import io.github.snorrefo.event_driven_social.shared.event.*
import io.github.snorrefo.event_driven_social.shared.infrastructure.config.AwsProperties
import io.github.snorrefo.event_driven_social.shared.infrastructure.config.SnsTopics
import io.github.snorrefo.event_driven_social.shared.infrastructure.config.SqsQueues
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import software.amazon.awssdk.services.sqs.SqsClient
import java.time.Instant
import java.util.*

class UserCountSqsConsumerTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    private val userRepository = mock<UserRepository>()

    private val consumer = UserCountSqsConsumer(
        sqsClient = mock<SqsClient>(),
        awsProperties = AwsProperties(
            region = "eu-north-1",
            sns = SnsTopics(postsCreated = "", usersFollowed = "", postsLiked = ""),
            sqs = SqsQueues(timelineEvents = "", notificationEvents = "", userCountEvents = "")
        ),
        objectMapper = objectMapper,
        userRepository = userRepository
    )

    @Test
    fun `should increment posts count on post created`() {
        val authorId = UUID.randomUUID()
        val event = PostCreatedEvent(
            data = PostCreatedData(
                postId = UUID.randomUUID().toString(),
                authorId = authorId.toString(),
                content = "Hello",
                createdAt = Instant.now()
            )
        )

        consumer.handleMessage("post.created", objectMapper.writeValueAsString(event))

        verify(userRepository).incrementPostsCount(authorId)
    }

    @Test
    fun `should increment follower and following counts on user followed`() {
        val followerId = UUID.randomUUID()
        val followedUserId = UUID.randomUUID()
        val event = UserFollowedEvent(
            data = UserFollowedData(
                followerId = followerId.toString(),
                followedUserId = followedUserId.toString(),
                followedAt = Instant.now()
            )
        )

        consumer.handleMessage("user.followed", objectMapper.writeValueAsString(event))

        verify(userRepository).incrementFollowersCount(followedUserId)
        verify(userRepository).incrementFollowingCount(followerId)
    }

    @Test
    fun `should decrement follower and following counts on user unfollowed`() {
        val followerId = UUID.randomUUID()
        val unfollowedUserId = UUID.randomUUID()
        val event = UserUnfollowedEvent(
            data = UserUnfollowedData(
                followerId = followerId.toString(),
                unfollowedUserId = unfollowedUserId.toString(),
                unfollowedAt = Instant.now()
            )
        )

        consumer.handleMessage("user.unfollowed", objectMapper.writeValueAsString(event))

        verify(userRepository).decrementFollowersCount(unfollowedUserId)
        verify(userRepository).decrementFollowingCount(followerId)
    }

    @Test
    fun `should ignore unknown event type`() {
        consumer.handleMessage("unknown.event", "{}")

        verifyNoInteractions(userRepository)
    }
}
