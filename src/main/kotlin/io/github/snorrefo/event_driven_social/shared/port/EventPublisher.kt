package io.github.snorrefo.event_driven_social.shared.port

import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry

interface EventPublisher {
    fun publish(entry: OutboxEntry)
}
