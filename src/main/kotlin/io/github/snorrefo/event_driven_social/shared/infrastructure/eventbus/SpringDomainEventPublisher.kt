package io.github.snorrefo.event_driven_social.shared.infrastructure.eventbus

import io.github.snorrefo.event_driven_social.shared.event.DomainEvent
import io.github.snorrefo.event_driven_social.shared.port.DomainEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class SpringDomainEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : DomainEventPublisher {
    override fun publish(event: DomainEvent) {
        applicationEventPublisher.publishEvent(event)
    }
}
