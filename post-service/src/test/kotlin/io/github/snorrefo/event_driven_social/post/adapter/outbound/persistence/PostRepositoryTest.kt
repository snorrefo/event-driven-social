package io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.post.TestContainersConfiguration
import io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.entity.PostJpaEntity
import io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.repository.PostJpaRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
class PostRepositoryTest {

    @Autowired
    lateinit var postJpaRepository: PostJpaRepository

    @Test
    fun `save and find post by ID`() {
        val authorId = UUID.randomUUID()
        val entity = PostJpaEntity(authorId = authorId, content = "Hello world")
        val saved = postJpaRepository.save(entity)

        val found = postJpaRepository.findById(saved.id).orElse(null)

        assertNotNull(found)
        assertEquals("Hello world", found.content)
    }

    @Test
    fun `find posts by author`() {
        val authorId = UUID.randomUUID()
        postJpaRepository.save(PostJpaEntity(authorId = authorId, content = "Post 1"))
        postJpaRepository.save(PostJpaEntity(authorId = authorId, content = "Post 2"))
        postJpaRepository.save(PostJpaEntity(authorId = UUID.randomUUID(), content = "Other"))

        val posts = postJpaRepository.findByAuthorIdOrderByCreatedAtDesc(authorId)

        assertEquals(2, posts.size)
    }
}
