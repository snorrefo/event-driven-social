package io.github.snorrefo.event_driven_social.timeline.adapter.inbound.web.dto

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import java.time.Instant
import java.util.UUID

data class TimelineEntryResponse(
    val postId: UUID,
    val authorId: UUID,
    val content: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(entry: TimelineEntry) = TimelineEntryResponse(
            postId = entry.postId, authorId = entry.authorId, content = entry.content, createdAt = entry.createdAt,
        )
    }
}
