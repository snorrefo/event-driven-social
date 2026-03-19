package io.github.snorrefo.event_driven_social.post.domain.model

import java.time.Instant
import java.util.UUID

data class Post(
    val id: UUID = UUID.randomUUID(),
    val authorId: UUID,
    val content: String,
    val createdAt: Instant = Instant.now(),
)
