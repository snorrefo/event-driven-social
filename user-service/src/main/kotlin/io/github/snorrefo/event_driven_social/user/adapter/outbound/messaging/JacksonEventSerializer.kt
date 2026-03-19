package io.github.snorrefo.event_driven_social.user.adapter.outbound.messaging

import tools.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.shared.event.DomainEvent
import io.github.snorrefo.event_driven_social.shared.port.EventSerializer
import org.springframework.stereotype.Component

@Component
class JacksonEventSerializer(
    private val objectMapper: ObjectMapper,
) : EventSerializer {

    override fun serialize(event: DomainEvent): String = objectMapper.writeValueAsString(event)

    override fun deserialize(json: String): DomainEvent = objectMapper.readValue(json, DomainEvent::class.java)
}
