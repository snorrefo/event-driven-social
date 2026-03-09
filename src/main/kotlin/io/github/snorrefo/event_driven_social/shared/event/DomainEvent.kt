package io.github.snorrefo.event_driven_social.shared.event

import java.time.Instant

sealed class DomainEvent {
    abstract val eventId: String
    abstract val timestamp: Instant
    abstract val version: String
}
