package io.github.snorrefo.event_driven_social.shared.port

import io.github.snorrefo.event_driven_social.shared.event.DomainEvent

interface DomainEventPublisher {
    fun publish(event: DomainEvent)
}
