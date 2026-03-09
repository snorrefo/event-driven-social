package io.github.snorrefo.event_driven_social.shared.infrastructure.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.shared.event.DomainEvent
import io.github.snorrefo.event_driven_social.shared.port.EventSerializer
import org.springframework.stereotype.Component

@Component
class JacksonEventSerializer(
    private val objectMapper: ObjectMapper
) : EventSerializer {
    override fun serialize(event: DomainEvent): String =
        objectMapper.writeValueAsString(event)
}
