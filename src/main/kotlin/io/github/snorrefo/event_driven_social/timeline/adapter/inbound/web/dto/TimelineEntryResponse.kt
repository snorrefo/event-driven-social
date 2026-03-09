package io.github.snorrefo.event_driven_social.timeline.adapter.inbound.web.dto

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import java.time.Instant

data class TimelineEntryResponse(
    val postId: String,
    val authorId: String,
    val content: String,
    val createdAt: Instant,
    val inReplyToPostId: String? = null,
    val mediaUrls: List<String> = emptyList()
)

fun TimelineEntry.toResponse() = TimelineEntryResponse(
    postId = postId.toString(),
    authorId = authorId.toString(),
    content = content,
    createdAt = createdAt,
    inReplyToPostId = inReplyToPostId?.toString(),
    mediaUrls = mediaUrls
)
