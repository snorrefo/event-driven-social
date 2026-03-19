package io.github.snorrefo.event_driven_social.user.domain.model

import java.time.Instant
import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val displayName: String,
    val createdAt: Instant = Instant.now(),
)
