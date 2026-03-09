package io.github.snorrefo.event_driven_social.shared.port

import io.github.snorrefo.event_driven_social.shared.event.DomainEvent

fun interface EventSerializer {
    fun serialize(event: DomainEvent): String
}
