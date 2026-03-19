package io.github.snorrefo.event_driven_social.shared.port

import io.github.snorrefo.event_driven_social.shared.event.DomainEvent

interface EventSerializer {
    fun serialize(event: DomainEvent): String
    fun deserialize(json: String): DomainEvent
}
