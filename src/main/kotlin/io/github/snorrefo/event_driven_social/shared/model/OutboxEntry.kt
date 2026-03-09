package io.github.snorrefo.event_driven_social.shared.model

import java.time.Instant
import java.util.*

data class OutboxEntry(
    val id: UUID = UUID.randomUUID(),
    val aggregateType: String,
    val aggregateId: UUID,
    val eventType: String,
    val payload: String,
    val createdAt: Instant = Instant.now(),
    val publishedAt: Instant? = null
)
