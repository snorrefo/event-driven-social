package io.github.snorrefo.event_driven_social.post.application

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.CreatePostUseCase
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.GetPostUseCase
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.GetPostsByAuthorsUseCase
import io.github.snorrefo.event_driven_social.post.domain.port.outbound.PostRepository
import io.github.snorrefo.event_driven_social.shared.event.PostCreatedData
import io.github.snorrefo.event_driven_social.shared.event.PostCreatedEvent
import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import io.github.snorrefo.event_driven_social.shared.port.DomainEventPublisher
import io.github.snorrefo.event_driven_social.shared.port.EventSerializer
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PostApplicationService(
    private val postRepository: PostRepository,
    private val outboxRepository: OutboxRepository,
    private val eventSerializer: EventSerializer,
    private val domainEventPublisher: DomainEventPublisher
) : CreatePostUseCase, GetPostUseCase, GetPostsByAuthorsUseCase {

    @Transactional
    override fun createPost(
        authorId: UUID,
        content: String,
        inReplyToPostId: UUID?,
        mediaUrls: List<String>
    ): Post {
        require(content.isNotBlank()) { "Post content cannot be blank" }
        require(content.length <= 280) { "Post content exceeds 280 characters" }

        val post = Post(
            authorId = authorId,
            content = content,
            inReplyToPostId = inReplyToPostId,
            mediaUrls = mediaUrls
        )

        val savedPost = postRepository.save(post)

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

        val outboxEntry = OutboxEntry(
            aggregateType = "post",
            aggregateId = savedPost.id,
            eventType = "post.created",
            payload = eventSerializer.serialize(event)
        )
        outboxRepository.save(outboxEntry)

        domainEventPublisher.publish(event)

        return savedPost
    }

    override fun getPost(postId: UUID): Post? =
        postRepository.findById(postId)

    override fun getUserPosts(authorId: UUID, page: Int, size: Int): List<Post> =
        postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, page, size)

    override fun getPostCount(authorId: UUID): Long =
        postRepository.countByAuthorId(authorId)

    override fun getPostsByAuthors(authorIds: List<UUID>, page: Int, size: Int): List<Post> =
        postRepository.findTimelineForUsers(authorIds, page, size)
}
