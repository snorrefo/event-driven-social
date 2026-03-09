package io.github.snorrefo.event_driven_social.shared.port

import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import java.time.Instant
import java.util.*

interface OutboxRepository {
    fun save(entry: OutboxEntry): OutboxEntry
    fun findPendingEvents(limit: Int): List<OutboxEntry>
    fun markPublished(entryId: UUID): OutboxEntry
    fun deletePublishedBefore(before: Instant): Int
}
