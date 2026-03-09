package io.github.snorrefo.event_driven_social.shared.infrastructure.outbox

import io.github.snorrefo.event_driven_social.shared.port.EventPublisher
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class OutboxScheduler(
    private val outboxRepository: OutboxRepository,
    private val eventPublisher: EventPublisher
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelay = 5000)
    @Transactional
    fun publishPendingEvents() {
        val pendingEvents = outboxRepository.findPendingEvents(100)

        if (pendingEvents.isEmpty()) {
            return
        }

        logger.info("Publishing ${pendingEvents.size} pending events")

        pendingEvents.forEach { entry ->
            try {
                eventPublisher.publish(entry)
                outboxRepository.markPublished(entry.id)
                logger.info("Published event: ${entry.id} (${entry.eventType})")
            } catch (e: Exception) {
                logger.error("Failed to publish event: ${entry.id}", e)
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    fun cleanupOldEvents() {
        val cutoff = Instant.now().minusSeconds(7 * 24 * 60 * 60)
        val deleted = outboxRepository.deletePublishedBefore(cutoff)
        logger.info("Cleaned up $deleted old outbox events")
    }
}
