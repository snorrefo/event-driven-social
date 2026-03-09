package io.github.snorrefo.event_driven_social.shared.event

import java.time.Instant
import java.util.*

data class PostCreatedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val timestamp: Instant = Instant.now(),
    override val version: String = "1.0",
    val data: PostCreatedData
) : DomainEvent()

data class PostCreatedData(
    val postId: String,
    val authorId: String,
    val content: String,
    val createdAt: Instant,
    val inReplyToPostId: String? = null,
    val mediaUrls: List<String> = emptyList()
)
