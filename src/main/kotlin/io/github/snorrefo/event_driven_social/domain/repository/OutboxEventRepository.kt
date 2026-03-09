package io.github.snorrefo.event_driven_social.domain.repository


import io.github.snorrefo.event_driven_social.domain.model.OutboxEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface OutboxEventRepository : JpaRepository<OutboxEvent, UUID> {

    fun findTop100ByPublishedAtIsNullOrderByCreatedAtAsc(): List<OutboxEvent>

    @Modifying
    @Query("DELETE FROM OutboxEvent e WHERE e.publishedAt < :before")
    fun deletePublishedBefore(@Param("before") before: Instant): Int
}