package io.github.snorrefo.event_driven_social.timeline.domain.model

import java.time.Instant
import java.util.*

data class TimelineEntry(
    val postId: UUID,
    val authorId: UUID,
    val content: String,
    val createdAt: Instant,
    val inReplyToPostId: UUID? = null,
    val mediaUrls: List<String> = emptyList()
)
