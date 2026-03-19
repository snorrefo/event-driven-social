package io.github.snorrefo.event_driven_social.post.application

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.CreatePostUseCase
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.GetPostUseCase
import io.github.snorrefo.event_driven_social.post.domain.port.outbound.PostRepository
import io.github.snorrefo.event_driven_social.shared.event.PostCreatedEvent
import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import io.github.snorrefo.event_driven_social.shared.port.EventSerializer
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class PostApplicationService(
    private val postRepository: PostRepository,
    private val outboxRepository: OutboxRepository,
    private val eventSerializer: EventSerializer,
) : CreatePostUseCase, GetPostUseCase {

    override fun createPost(authorId: UUID, content: String): Post {
        require(content.isNotBlank()) { "Post content cannot be blank" }
        require(content.length <= 280) { "Post content cannot exceed 280 characters" }

        val post = Post(authorId = authorId, content = content)
        val saved = postRepository.save(post)

        val event = PostCreatedEvent(postId = saved.id, authorId = saved.authorId, content = saved.content)
        outboxRepository.save(
            OutboxEntry(eventType = event.eventType, payload = eventSerializer.serialize(event))
        )

        return saved
    }

    @Transactional(readOnly = true)
    override fun getPost(id: UUID): Post =
        postRepository.findById(id) ?: throw NoSuchElementException("Post not found: $id")

    @Transactional(readOnly = true)
    override fun getPostsByAuthor(authorId: UUID): List<Post> =
        postRepository.findByAuthorId(authorId)
}
