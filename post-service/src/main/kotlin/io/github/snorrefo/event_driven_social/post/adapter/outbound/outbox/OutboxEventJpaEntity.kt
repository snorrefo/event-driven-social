package io.github.snorrefo.event_driven_social.post.adapter.outbound.outbox

import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "outbox_events")
class OutboxEventJpaEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column(nullable = false) val eventType: String = "",
    @Column(nullable = false, columnDefinition = "TEXT") val payload: String = "",
    @Column(nullable = false) val createdAt: Instant = Instant.now(),
    @Column(nullable = false) val published: Boolean = false,
) {
    fun toDomain() = OutboxEntry(id = id, eventType = eventType, payload = payload, createdAt = createdAt, published = published)

    companion object {
        fun fromDomain(entry: OutboxEntry) = OutboxEventJpaEntity(
            id = entry.id, eventType = entry.eventType, payload = entry.payload, createdAt = entry.createdAt, published = entry.published,
        )
    }
}
