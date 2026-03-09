package io.github.snorrefo.event_driven_social.post.domain.model

import java.time.Instant
import java.util.*

data class Post(
    val id: UUID = UUID.randomUUID(),
    val authorId: UUID,
    val content: String,
    val createdAt: Instant = Instant.now(),
    val inReplyToPostId: UUID? = null,
    val mediaUrls: List<String> = emptyList()
)
