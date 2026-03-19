package io.github.snorrefo.event_driven_social.shared.model

import java.time.Instant
import java.util.UUID

data class OutboxEntry(
    val id: UUID = UUID.randomUUID(),
    val eventType: String,
    val payload: String,
    val createdAt: Instant = Instant.now(),
    val published: Boolean = false,
)
