package io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.entity.PostJpaEntity
import io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.repository.PostJpaRepository
import io.github.snorrefo.event_driven_social.post.domain.model.Post
import io.github.snorrefo.event_driven_social.post.domain.port.outbound.PostRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PostRepositoryAdapter(
    private val jpaRepository: PostJpaRepository,
) : PostRepository {

    override fun save(post: Post): Post =
        jpaRepository.save(PostJpaEntity.fromDomain(post)).toDomain()

    override fun findById(id: UUID): Post? =
        jpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByAuthorId(authorId: UUID): List<Post> =
        jpaRepository.findByAuthorIdOrderByCreatedAtDesc(authorId).map { it.toDomain() }
}
