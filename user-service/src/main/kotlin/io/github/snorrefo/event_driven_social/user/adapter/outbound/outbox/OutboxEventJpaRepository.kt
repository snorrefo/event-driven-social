package io.github.snorrefo.event_driven_social.user.adapter.outbound.outbox

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface OutboxEventJpaRepository : JpaRepository<OutboxEventJpaEntity, UUID> {
    fun findByPublishedFalseOrderByCreatedAtAsc(): List<OutboxEventJpaEntity>

    @Modifying
    @Query("UPDATE OutboxEventJpaEntity e SET e.published = true WHERE e.id = :id")
    fun markPublished(id: UUID)
}
