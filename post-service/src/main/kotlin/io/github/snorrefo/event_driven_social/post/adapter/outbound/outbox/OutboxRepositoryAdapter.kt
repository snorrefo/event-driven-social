package io.github.snorrefo.event_driven_social.post.adapter.outbound.outbox

import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class OutboxRepositoryAdapter(
    private val jpaRepository: OutboxEventJpaRepository,
) : OutboxRepository {

    override fun save(entry: OutboxEntry): OutboxEntry =
        jpaRepository.save(OutboxEventJpaEntity.fromDomain(entry)).toDomain()

    override fun findUnpublished(): List<OutboxEntry> =
        jpaRepository.findByPublishedFalseOrderByCreatedAtAsc().map { it.toDomain() }

    override fun markPublished(id: UUID) =
        jpaRepository.markPublished(id)
}
