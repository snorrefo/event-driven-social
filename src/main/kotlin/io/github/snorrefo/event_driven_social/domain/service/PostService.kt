package io.github.snorrefo.event_driven_social.domain.service


import com.fasterxml.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.domain.model.OutboxEvent
import io.github.snorrefo.event_driven_social.domain.model.Post
import io.github.snorrefo.event_driven_social.domain.repository.OutboxEventRepository
import io.github.snorrefo.event_driven_social.domain.repository.PostRepository
import io.github.snorrefo.event_driven_social.events.PostCreatedData
import io.github.snorrefo.event_driven_social.events.PostCreatedEvent
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PostService(
    private val postRepository: PostRepository,
    private val outboxRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper
) {

    /**
     * One Kotlin-specific gotcha:
     * Spring's @Transactional works through proxies,
     * and by default Kotlin classes and methods are final. You
     * need the kotlin-spring compiler plugin
     * (included automatically if you generate your project from start.spring.io)
     * which opens Spring-annotated classes and their methods so that proxying works correctly.
     * Without it, @Transactional silently does nothing because Spring can't subclass your service to wrap it.
     * */
    @Transactional
    fun createPost(
        authorId: UUID,
        content: String,
        inReplyToPostId: UUID? = null,
        mediaUrls: List<String> = emptyList()
    ): Post {
        // Validate
        require(content.isNotBlank()) { "Post content cannot be blank" }
        require(content.length <= 280) { "Post content exceeds 280 characters" }

        // Create post
        val post = Post(
            authorId = authorId,
            content = content,
            inReplyToPostId = inReplyToPostId,
            mediaUrls = mediaUrls
        )

        // Save to database
        val savedPost = postRepository.save(post)

        // Create event
        val event = PostCreatedEvent(
            data = PostCreatedData(
                postId = savedPost.id.toString(),
                authorId = savedPost.authorId.toString(),
                content = savedPost.content,
                createdAt = savedPost.createdAt,
                inReplyToPostId = savedPost.inReplyToPostId?.toString(),
                mediaUrls = savedPost.mediaUrls
            )
        )

        // Save to outbox (transactional outbox pattern)
        val outboxEvent = OutboxEvent(
            aggregateType = "post",
            aggregateId = savedPost.id,
            eventType = "post.created",
            payload = objectMapper.writeValueAsString(event)
        )
        outboxRepository.save(outboxEvent)

        return savedPost
    }

    fun getPost(postId: UUID): Post? {
        return postRepository.findById(postId).orElse(null)
    }

    fun getUserPosts(authorId: UUID, page: Int = 0, size: Int = 20): List<Post> {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(
            authorId,
            PageRequest.of(page, size)
        )
    }

    fun getPostCount(authorId: UUID): Long {
        return postRepository.countByAuthorId(authorId)
    }
}