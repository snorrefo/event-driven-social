package io.github.snorrefo.event_driven_social.timeline.domain.port.outbound

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import java.util.UUID

interface TimelineRepository {
    fun addEntry(userId: UUID, entry: TimelineEntry)
    fun getEntries(userId: UUID, offset: Long, limit: Long): List<TimelineEntry>
    fun removeEntriesByAuthor(userId: UUID, authorId: UUID)
}
