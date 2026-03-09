package io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.entity.PostJpaEntity
import io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.repository.PostJpaRepository
import io.github.snorrefo.event_driven_social.post.domain.model.Post
import io.github.snorrefo.event_driven_social.post.domain.port.outbound.PostRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class PostRepositoryAdapter(
    private val jpaRepository: PostJpaRepository
) : PostRepository {

    override fun save(post: Post): Post =
        jpaRepository.save(post.toJpaEntity()).toDomain()

    override fun findById(postId: UUID): Post? =
        jpaRepository.findById(postId).orElse(null)?.toDomain()

    override fun findByAuthorIdOrderByCreatedAtDesc(authorId: UUID, page: Int, size: Int): List<Post> =
        jpaRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, PageRequest.of(page, size))
            .map { it.toDomain() }

    override fun findTimelineForUsers(authorIds: List<UUID>, page: Int, size: Int): List<Post> =
        jpaRepository.findTimelineForUsers(authorIds, PageRequest.of(page, size))
            .map { it.toDomain() }

    override fun countByAuthorId(authorId: UUID): Long =
        jpaRepository.countByAuthorId(authorId)
}

private fun Post.toJpaEntity() = PostJpaEntity(
    id = id,
    authorId = authorId,
    content = content,
    createdAt = createdAt,
    inReplyToPostId = inReplyToPostId,
    mediaUrls = mediaUrls
)

private fun PostJpaEntity.toDomain() = Post(
    id = id,
    authorId = authorId,
    content = content,
    createdAt = createdAt,
    inReplyToPostId = inReplyToPostId,
    mediaUrls = mediaUrls
)
