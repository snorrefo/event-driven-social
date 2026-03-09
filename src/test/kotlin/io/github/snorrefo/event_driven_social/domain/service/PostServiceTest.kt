package io.github.snorrefo.event_driven_social.domain.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.snorrefo.event_driven_social.domain.model.OutboxEvent
import io.github.snorrefo.event_driven_social.domain.model.Post
import io.github.snorrefo.event_driven_social.domain.repository.OutboxEventRepository
import io.github.snorrefo.event_driven_social.domain.repository.PostRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PostServiceTest {

    private val postRepository = mock<PostRepository>()
    private val outboxRepository = mock<OutboxEventRepository>()
    private val objectMapper = jacksonObjectMapper().findAndRegisterModules()

    private val postService = PostService(postRepository, outboxRepository, objectMapper)

    @Test
    fun `should create post and outbox event`() {
        val authorId = UUID.randomUUID()
        val content = "Test post"

        whenever(postRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = postService.createPost(authorId, content)

        assertNotNull(result)
        assertEquals(content, result.content)
        assertEquals(authorId, result.authorId)
        verify(postRepository).save(any())
        verify(outboxRepository).save(any())
    }

    @Test
    fun `should create post with media URLs`() {
        val authorId = UUID.randomUUID()
        val content = "Check out this photo!"
        val mediaUrls = listOf("https://example.com/photo1.jpg", "https://example.com/photo2.jpg")

        whenever(postRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = postService.createPost(authorId, content, mediaUrls = mediaUrls)

        assertEquals(2, result.mediaUrls.size)
        assertEquals(mediaUrls, result.mediaUrls)
    }

    @Test
    fun `should create reply post`() {
        val authorId = UUID.randomUUID()
        val originalPostId = UUID.randomUUID()
        val content = "Great point!"

        whenever(postRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = postService.createPost(
            authorId = authorId,
            content = content,
            inReplyToPostId = originalPostId
        )

        assertEquals(originalPostId, result.inReplyToPostId)
    }

    @Test
    fun `should reject blank post content`() {
        val authorId = UUID.randomUUID()

        val exception = assertThrows<IllegalArgumentException> {
            postService.createPost(authorId, "   ")
        }

        assertEquals("Post content cannot be blank", exception.message)
        verify(postRepository, never()).save(any())
    }

    @Test
    fun `should reject post exceeding 280 characters`() {
        val authorId = UUID.randomUUID()

        val exception = assertThrows<IllegalArgumentException> {
            postService.createPost(authorId, "a".repeat(281))
        }

        assertEquals("Post content exceeds 280 characters", exception.message)
        verify(postRepository, never()).save(any())
    }

    @Test
    fun `should accept post with exactly 280 characters`() {
        val authorId = UUID.randomUUID()

        whenever(postRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = postService.createPost(authorId, "a".repeat(280))

        assertEquals(280, result.content.length)
    }

    @Test
    fun `should save event to outbox with correct structure`() {
        val authorId = UUID.randomUUID()
        val captor = argumentCaptor<OutboxEvent>()

        whenever(postRepository.save(any())).thenAnswer { it.arguments[0] }
        whenever(outboxRepository.save(any())).thenAnswer { it.arguments[0] }

        postService.createPost(authorId, "Test post")

        verify(outboxRepository).save(captor.capture())
        val outboxEvent = captor.firstValue
        assertEquals("post", outboxEvent.aggregateType)
        assertEquals("post.created", outboxEvent.eventType)
        assertNotNull(outboxEvent.payload)
        assertNull(outboxEvent.publishedAt)
    }

    @Test
    fun `should get post by id when exists`() {
        val postId = UUID.randomUUID()
        val post = Post(id = postId, authorId = UUID.randomUUID(), content = "Test post")

        whenever(postRepository.findById(postId)).thenReturn(Optional.of(post))

        val result = postService.getPost(postId)

        assertNotNull(result)
        assertEquals(postId, result.id)
    }

    @Test
    fun `should return null when post does not exist`() {
        val postId = UUID.randomUUID()

        whenever(postRepository.findById(postId)).thenReturn(Optional.empty())

        val result = postService.getPost(postId)

        assertNull(result)
    }
}
