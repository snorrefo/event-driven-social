package io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.repository

import io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.entity.PostJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostJpaRepository : JpaRepository<PostJpaEntity, UUID> {
    fun findByAuthorIdOrderByCreatedAtDesc(authorId: UUID): List<PostJpaEntity>
}
