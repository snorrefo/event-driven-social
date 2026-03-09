package io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.repository

import io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.entity.PostJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostJpaRepository : JpaRepository<PostJpaEntity, UUID> {

    fun findByAuthorIdOrderByCreatedAtDesc(authorId: UUID, pageable: Pageable): List<PostJpaEntity>

    @Query(
        """
        SELECT t FROM PostJpaEntity t
        WHERE t.authorId IN :authorIds
        ORDER BY t.createdAt DESC
    """
    )
    fun findTimelineForUsers(
        @Param("authorIds") authorIds: List<UUID>,
        pageable: Pageable
    ): List<PostJpaEntity>

    fun countByAuthorId(authorId: UUID): Long
}
