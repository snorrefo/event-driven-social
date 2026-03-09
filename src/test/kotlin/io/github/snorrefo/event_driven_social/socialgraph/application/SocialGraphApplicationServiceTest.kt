package io.github.snorrefo.event_driven_social.socialgraph.application

import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import io.github.snorrefo.event_driven_social.shared.port.DomainEventPublisher
import io.github.snorrefo.event_driven_social.shared.port.EventSerializer
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import io.github.snorrefo.event_driven_social.socialgraph.domain.model.Follow
import io.github.snorrefo.event_driven_social.socialgraph.domain.port.outbound.FollowRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SocialGraphApplicationServiceTest {

    private val followRepository = mock<FollowRepository>()
    private val outboxRepository = mock<OutboxRepository>()
    private val eventSerializer = EventSerializer { """{"type":"test"}""" }
    private val domainEventPublisher = mock<DomainEventPublisher>()

    private val service = SocialGraphApplicationService(
        followRepository, outboxRepository, eventSerializer, domainEventPublisher
    )

    @Test
    fun `should follow a user and publish event`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()

        whenever(followRepository.exists(followerId, followedId)).thenReturn(false)
        whenever(followRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = service.follow(followerId, followedId)

        assertNotNull(result)
        assertEquals(followerId, result.followerId)
        assertEquals(followedId, result.followedId)
        verify(followRepository).save(any())
        verify(outboxRepository).save(any())
        verify(domainEventPublisher).publish(any())
    }

    @Test
    fun `should save follow outbox entry with correct structure`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()
        val captor = argumentCaptor<OutboxEntry>()

        whenever(followRepository.exists(followerId, followedId)).thenReturn(false)
        whenever(followRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        service.follow(followerId, followedId)

        verify(outboxRepository).save(captor.capture())
        val outboxEntry = captor.firstValue
        assertEquals("follow", outboxEntry.aggregateType)
        assertEquals("user.followed", outboxEntry.eventType)
        assertEquals(followerId, outboxEntry.aggregateId)
    }

    @Test
    fun `should reject self-follow`() {
        val userId = UUID.randomUUID()

        val exception = assertThrows<IllegalArgumentException> {
            service.follow(userId, userId)
        }

        assertEquals("Users cannot follow themselves", exception.message)
        verify(followRepository, never()).save(any())
    }

    @Test
    fun `should reject duplicate follow`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()

        whenever(followRepository.exists(followerId, followedId)).thenReturn(true)

        val exception = assertThrows<IllegalArgumentException> {
            service.follow(followerId, followedId)
        }

        assertEquals("Already following this user", exception.message)
        verify(followRepository, never()).save(any())
    }

    @Test
    fun `should unfollow a user and publish event`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()

        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        service.unfollow(followerId, followedId)

        verify(followRepository).delete(followerId, followedId)
        verify(outboxRepository).save(any())
        verify(domainEventPublisher).publish(any())
    }

    @Test
    fun `should save unfollow outbox entry with correct structure`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()
        val captor = argumentCaptor<OutboxEntry>()

        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        service.unfollow(followerId, followedId)

        verify(outboxRepository).save(captor.capture())
        val outboxEntry = captor.firstValue
        assertEquals("follow", outboxEntry.aggregateType)
        assertEquals("user.unfollowed", outboxEntry.eventType)
        assertEquals(followerId, outboxEntry.aggregateId)
    }

    @Test
    fun `should get followed user ids`() {
        val userId = UUID.randomUUID()
        val followedIds = listOf(UUID.randomUUID(), UUID.randomUUID())

        whenever(followRepository.findFollowedUserIds(userId)).thenReturn(followedIds)

        val result = service.getFollowedUserIds(userId)

        assertEquals(followedIds, result)
    }

    @Test
    fun `should return empty list when not following anyone`() {
        val userId = UUID.randomUUID()

        whenever(followRepository.findFollowedUserIds(userId)).thenReturn(emptyList())

        val result = service.getFollowedUserIds(userId)

        assertEquals(emptyList(), result)
    }
}
