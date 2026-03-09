package io.github.snorrefo.event_driven_social.domain.repository


import io.github.snorrefo.event_driven_social.domain.TestContainersConfiguration
import io.github.snorrefo.event_driven_social.domain.model.Post
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

// Repository Tests (Integration Tests with Testcontainers)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private lateinit var postRepository: PostRepository

    @Test
    fun `should save and retrieve post`() {
        // Given
        val authorId = UUID.randomUUID()
        val post = Post(
            authorId = authorId,
            content = "Hello, world!"
        )

        // When
        val saved = postRepository.save(post)
        val found = postRepository.findById(saved.id)

        // Then
        assertTrue(found.isPresent)
        assertEquals(post.content, found.get().content)
        assertEquals(authorId, found.get().authorId)
        assertNotNull(found.get().createdAt)
    }

    @Test
    fun `should save post with media URLs`() {
        // Given
        val post = Post(
            authorId = UUID.randomUUID(),
            content = "Check this out!",
            mediaUrls = listOf("https://example.com/photo1.jpg", "https://example.com/photo2.jpg")
        )

        // When
        val saved = postRepository.save(post)
        val found = postRepository.findById(saved.id)

        // Then
        assertTrue(found.isPresent)
        assertEquals(2, found.get().mediaUrls.size)
    }

    @Test
    fun `should find posts by author ordered by created date`() {
        // Given
        val authorId = UUID.randomUUID()

        // Create posts with slight delays to ensure different timestamps
        val post1 = postRepository.save(Post(authorId = authorId, content = "First post"))
        Thread.sleep(10)
        val post2 = postRepository.save(Post(authorId = authorId, content = "Second post"))
        Thread.sleep(10)
        val post3 = postRepository.save(Post(authorId = authorId, content = "Third post"))

        // When
        val posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(
            authorId,
            PageRequest.of(0, 10)
        )

        // Then
        assertEquals(3, posts.size)
        assertEquals("Third post", posts[0].content)  // Most recent first
        assertEquals("Second post", posts[1].content)
        assertEquals("First post", posts[2].content)
    }

    @Test
    fun `should paginate user posts`() {
        // Given
        val authorId = UUID.randomUUID()
        repeat(5) { i ->
            postRepository.save(Post(authorId = authorId, content = "Post $i"))
            Thread.sleep(10)
        }

        // When - Get first page (2 items)
        val page1 = postRepository.findByAuthorIdOrderByCreatedAtDesc(
            authorId,
            PageRequest.of(0, 2)
        )

        // Then
        assertEquals(2, page1.size)

        // When - Get second page (2 items)
        val page2 = postRepository.findByAuthorIdOrderByCreatedAtDesc(
            authorId,
            PageRequest.of(1, 2)
        )

        // Then
        assertEquals(2, page2.size)

        // Verify different posts on each page
        assertTrue(page1[0].id != page2[0].id)
    }

    @Test
    fun `should find timeline for multiple users`() {
        // Given
        val user1 = UUID.randomUUID()
        val user2 = UUID.randomUUID()
        val user3 = UUID.randomUUID()

        postRepository.save(Post(authorId = user1, content = "User 1 post"))
        postRepository.save(Post(authorId = user2, content = "User 2 post"))
        postRepository.save(Post(authorId = user3, content = "User 3 post"))

        // When - Get timeline for user1 and user2 only
        val timeline = postRepository.findTimelineForUsers(
            listOf(user1, user2),
            PageRequest.of(0, 10)
        )

        // Then
        assertEquals(2, timeline.size)
        assertTrue(timeline.any { it.content == "User 1 post" })
        assertTrue(timeline.any { it.content == "User 2 post" })
        assertTrue(timeline.none { it.content == "User 3 post" })
    }

    @Test
    fun `should count posts by author`() {
        // Given
        val authorId = UUID.randomUUID()
        repeat(3) {
            postRepository.save(Post(authorId = authorId, content = "Post"))
        }

        // When
        val count = postRepository.countByAuthorId(authorId)

        // Then
        assertEquals(3, count)
    }

    @Test
    fun `should save reply post with reference to original`() {
        // Given
        val originalPost = postRepository.save(
            Post(authorId = UUID.randomUUID(), content = "Original post")
        )

        val reply = Post(
            authorId = UUID.randomUUID(),
            content = "Reply post",
            inReplyToPostId = originalPost.id
        )

        // When
        val savedReply = postRepository.save(reply)
        val found = postRepository.findById(savedReply.id)

        // Then
        assertTrue(found.isPresent)
        assertEquals(originalPost.id, found.get().inReplyToPostId)
    }
}