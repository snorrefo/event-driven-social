package io.github.snorrefo.event_driven_social.user.application

import io.github.snorrefo.event_driven_social.shared.port.EventSerializer
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import io.github.snorrefo.event_driven_social.user.domain.port.outbound.FollowRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

class SocialGraphApplicationServiceTest {

    private val followRepository: FollowRepository = mock()
    private val outboxRepository: OutboxRepository = mock()
    private val eventSerializer: EventSerializer = mock()
    private val service = SocialGraphApplicationService(followRepository, outboxRepository, eventSerializer)

    @Test
    fun `follow saves follow and outbox entry`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()
        whenever(followRepository.exists(followerId, followedId)).thenReturn(false)
        whenever(eventSerializer.serialize(any())).thenReturn("{}")
        whenever(followRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        service.follow(followerId, followedId)

        verify(followRepository).save(any())
        verify(outboxRepository).save(any())
    }

    @Test
    fun `follow throws when following yourself`() {
        val id = UUID.randomUUID()

        assertThrows<IllegalArgumentException> {
            service.follow(id, id)
        }
    }

    @Test
    fun `follow throws when already following`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()
        whenever(followRepository.exists(followerId, followedId)).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            service.follow(followerId, followedId)
        }
    }

    @Test
    fun `unfollow deletes follow and saves outbox entry`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()
        whenever(followRepository.exists(followerId, followedId)).thenReturn(true)
        whenever(eventSerializer.serialize(any())).thenReturn("{}")
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        service.unfollow(followerId, followedId)

        verify(followRepository).delete(followerId, followedId)
        verify(outboxRepository).save(any())
    }

    @Test
    fun `unfollow throws when not following`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()
        whenever(followRepository.exists(followerId, followedId)).thenReturn(false)

        assertThrows<IllegalArgumentException> {
            service.unfollow(followerId, followedId)
        }
    }
}
