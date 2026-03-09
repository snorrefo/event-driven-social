package io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.repository.PostJpaRepository
import io.github.snorrefo.event_driven_social.post.domain.model.Post
import io.github.snorrefo.event_driven_social.shared.TestContainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private lateinit var postJpaRepository: PostJpaRepository

    private val postRepository by lazy { PostRepositoryAdapter(postJpaRepository) }

    @Test
    fun `should save and retrieve post`() {
        val authorId = UUID.randomUUID()
        val post = Post(authorId = authorId, content = "Hello, world!")

        val saved = postRepository.save(post)
        val found = postRepository.findById(saved.id)

        assertNotNull(found)
        assertEquals(post.content, found.content)
        assertEquals(authorId, found.authorId)
        assertNotNull(found.createdAt)
    }

    @Test
    fun `should save post with media URLs`() {
        val post = Post(
            authorId = UUID.randomUUID(),
            content = "Check this out!",
            mediaUrls = listOf("https://example.com/photo1.jpg", "https://example.com/photo2.jpg")
        )

        val saved = postRepository.save(post)
        val found = postRepository.findById(saved.id)

        assertNotNull(found)
        assertEquals(2, found.mediaUrls.size)
    }

    @Test
    fun `should find posts by author ordered by created date`() {
        val authorId = UUID.randomUUID()

        postRepository.save(Post(authorId = authorId, content = "First post"))
        Thread.sleep(10)
        postRepository.save(Post(authorId = authorId, content = "Second post"))
        Thread.sleep(10)
        postRepository.save(Post(authorId = authorId, content = "Third post"))

        val posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, 0, 10)

        assertEquals(3, posts.size)
        assertEquals("Third post", posts[0].content)
        assertEquals("Second post", posts[1].content)
        assertEquals("First post", posts[2].content)
    }

    @Test
    fun `should paginate user posts`() {
        val authorId = UUID.randomUUID()
        repeat(5) { i ->
            postRepository.save(Post(authorId = authorId, content = "Post $i"))
            Thread.sleep(10)
        }

        val page1 = postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, 0, 2)
        assertEquals(2, page1.size)

        val page2 = postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, 1, 2)
        assertEquals(2, page2.size)

        assertTrue(page1[0].id != page2[0].id)
    }

    @Test
    fun `should find timeline for multiple users`() {
        val user1 = UUID.randomUUID()
        val user2 = UUID.randomUUID()
        val user3 = UUID.randomUUID()

        postRepository.save(Post(authorId = user1, content = "User 1 post"))
        postRepository.save(Post(authorId = user2, content = "User 2 post"))
        postRepository.save(Post(authorId = user3, content = "User 3 post"))

        val timeline = postRepository.findTimelineForUsers(listOf(user1, user2), 0, 10)

        assertEquals(2, timeline.size)
        assertTrue(timeline.any { it.content == "User 1 post" })
        assertTrue(timeline.any { it.content == "User 2 post" })
        assertTrue(timeline.none { it.content == "User 3 post" })
    }

    @Test
    fun `should count posts by author`() {
        val authorId = UUID.randomUUID()
        repeat(3) {
            postRepository.save(Post(authorId = authorId, content = "Post"))
        }

        val count = postRepository.countByAuthorId(authorId)

        assertEquals(3, count)
    }

    @Test
    fun `should save reply post with reference to original`() {
        val originalPost = postRepository.save(
            Post(authorId = UUID.randomUUID(), content = "Original post")
        )

        val reply = Post(
            authorId = UUID.randomUUID(),
            content = "Reply post",
            inReplyToPostId = originalPost.id
        )

        val savedReply = postRepository.save(reply)
        val found = postRepository.findById(savedReply.id)

        assertNotNull(found)
        assertEquals(originalPost.id, found.inReplyToPostId)
    }
}
