package io.github.snorrefo.event_driven_social.shared.infrastructure.outbox

import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class OutboxRepositoryAdapter(
    private val jpaRepository: OutboxEventJpaRepository
) : OutboxRepository {

    override fun save(entry: OutboxEntry): OutboxEntry =
        jpaRepository.save(entry.toJpaEntity()).toDomain()

    override fun findPendingEvents(limit: Int): List<OutboxEntry> =
        jpaRepository.findTop100ByPublishedAtIsNullOrderByCreatedAtAsc()
            .take(limit)
            .map { it.toDomain() }

    override fun markPublished(entryId: UUID): OutboxEntry {
        val entity = jpaRepository.findById(entryId)
            .orElseThrow { IllegalArgumentException("Outbox entry not found: $entryId") }
        entity.publishedAt = Instant.now()
        return jpaRepository.save(entity).toDomain()
    }

    override fun deletePublishedBefore(before: Instant): Int =
        jpaRepository.deletePublishedBefore(before)
}

private fun OutboxEntry.toJpaEntity() = OutboxEventJpaEntity(
    id = id,
    aggregateType = aggregateType,
    aggregateId = aggregateId,
    eventType = eventType,
    payload = payload,
    createdAt = createdAt,
    publishedAt = publishedAt
)

private fun OutboxEventJpaEntity.toDomain() = OutboxEntry(
    id = id,
    aggregateType = aggregateType,
    aggregateId = aggregateId,
    eventType = eventType,
    payload = payload,
    createdAt = createdAt,
    publishedAt = publishedAt
)
