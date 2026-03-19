package io.github.snorrefo.event_driven_social.timeline.domain.model

import java.time.Instant
import java.util.UUID

data class TimelineEntry(
    val postId: UUID,
    val authorId: UUID,
    val content: String,
    val createdAt: Instant,
)
