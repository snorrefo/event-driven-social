package io.github.snorrefo.event_driven_social.post.application

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import io.github.snorrefo.event_driven_social.post.domain.port.outbound.PostRepository
import io.github.snorrefo.event_driven_social.shared.port.EventSerializer
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.assertEquals

class PostApplicationServiceTest {

    private val postRepository: PostRepository = mock()
    private val outboxRepository: OutboxRepository = mock()
    private val eventSerializer: EventSerializer = mock()
    private val service = PostApplicationService(postRepository, outboxRepository, eventSerializer)

    @Test
    fun `createPost saves post and outbox entry`() {
        val authorId = UUID.randomUUID()
        val post = Post(authorId = authorId, content = "Hello world")
        whenever(postRepository.save(any())).thenReturn(post)
        whenever(eventSerializer.serialize(any())).thenReturn("{}")
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = service.createPost(authorId, "Hello world")

        assertEquals("Hello world", result.content)
        verify(postRepository).save(any())
        verify(outboxRepository).save(any())
    }

    @Test
    fun `createPost throws for blank content`() {
        assertThrows<IllegalArgumentException> {
            service.createPost(UUID.randomUUID(), "   ")
        }
    }

    @Test
    fun `createPost throws for content over 280 chars`() {
        assertThrows<IllegalArgumentException> {
            service.createPost(UUID.randomUUID(), "a".repeat(281))
        }
    }

    @Test
    fun `getPost returns post when found`() {
        val id = UUID.randomUUID()
        val post = Post(id = id, authorId = UUID.randomUUID(), content = "Hi")
        whenever(postRepository.findById(id)).thenReturn(post)

        val result = service.getPost(id)

        assertEquals(id, result.id)
    }

    @Test
    fun `getPost throws when not found`() {
        val id = UUID.randomUUID()
        whenever(postRepository.findById(id)).thenReturn(null)

        assertThrows<NoSuchElementException> {
            service.getPost(id)
        }
    }
}
