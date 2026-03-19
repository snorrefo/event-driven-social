package io.github.snorrefo.event_driven_social.shared.port

import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import java.util.UUID

interface OutboxRepository {
    fun save(entry: OutboxEntry): OutboxEntry
    fun findUnpublished(): List<OutboxEntry>
    fun markPublished(id: UUID)
}
