package io.github.snorrefo.event_driven_social.shared.infrastructure.outbox

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface OutboxEventJpaRepository : JpaRepository<OutboxEventJpaEntity, UUID> {

    fun findTop100ByPublishedAtIsNullOrderByCreatedAtAsc(): List<OutboxEventJpaEntity>

    @Modifying
    @Query("DELETE FROM OutboxEventJpaEntity e WHERE e.publishedAt < :before")
    fun deletePublishedBefore(@Param("before") before: Instant): Int
}
