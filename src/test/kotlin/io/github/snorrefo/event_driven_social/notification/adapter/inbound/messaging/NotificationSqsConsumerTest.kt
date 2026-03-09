package io.github.snorrefo.event_driven_social.notification.adapter.inbound.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.snorrefo.event_driven_social.notification.domain.model.NotificationType
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.CreateNotificationUseCase
import io.github.snorrefo.event_driven_social.shared.event.PostCreatedData
import io.github.snorrefo.event_driven_social.shared.event.PostCreatedEvent
import io.github.snorrefo.event_driven_social.shared.event.UserFollowedData
import io.github.snorrefo.event_driven_social.shared.event.UserFollowedEvent
import io.github.snorrefo.event_driven_social.shared.infrastructure.config.AwsProperties
import io.github.snorrefo.event_driven_social.shared.infrastructure.config.SnsTopics
import io.github.snorrefo.event_driven_social.shared.infrastructure.config.SqsQueues
import io.github.snorrefo.event_driven_social.socialgraph.domain.port.inbound.GetFollowersUseCase
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import software.amazon.awssdk.services.sqs.SqsClient
import java.time.Instant
import java.util.*

class NotificationSqsConsumerTest {

    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    private val createNotificationUseCase = mock<CreateNotificationUseCase>()
    private val getFollowersUseCase = mock<GetFollowersUseCase>()

    private val consumer = NotificationSqsConsumer(
        sqsClient = mock<SqsClient>(),
        awsProperties = AwsProperties(
            region = "eu-north-1",
            sns = SnsTopics(postsCreated = "", usersFollowed = "", postsLiked = ""),
            sqs = SqsQueues(timelineEvents = "", notificationEvents = "", userCountEvents = "")
        ),
        objectMapper = objectMapper,
        createNotificationUseCase = createNotificationUseCase,
        getFollowersUseCase = getFollowersUseCase
    )

    @Test
    fun `should create notification for user followed event`() {
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

        verify(createNotificationUseCase).createNotification(
            userId = eq(followedUserId),
            type = eq(NotificationType.NEW_FOLLOWER),
            actorId = eq(followerId),
            referenceId = isNull()
        )
    }

    @Test
    fun `should create notifications for post created event`() {
        val authorId = UUID.randomUUID()
        val postId = UUID.randomUUID()
        val follower1 = UUID.randomUUID()
        val follower2 = UUID.randomUUID()

        whenever(getFollowersUseCase.getFollowerIds(authorId)).thenReturn(listOf(follower1, follower2))

        val event = PostCreatedEvent(
            data = PostCreatedData(
                postId = postId.toString(),
                authorId = authorId.toString(),
                content = "Hello world",
                createdAt = Instant.now()
            )
        )

        consumer.handleMessage("post.created", objectMapper.writeValueAsString(event))

        verify(createNotificationUseCase).createNotification(
            userId = eq(follower1),
            type = eq(NotificationType.NEW_POST),
            actorId = eq(authorId),
            referenceId = eq(postId)
        )
        verify(createNotificationUseCase).createNotification(
            userId = eq(follower2),
            type = eq(NotificationType.NEW_POST),
            actorId = eq(authorId),
            referenceId = eq(postId)
        )
    }

    @Test
    fun `should ignore unknown event type`() {
        consumer.handleMessage("unknown.event", "{}")

        verifyNoInteractions(createNotificationUseCase)
    }
}
