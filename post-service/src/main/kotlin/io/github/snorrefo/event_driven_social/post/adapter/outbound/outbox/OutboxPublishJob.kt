package io.github.snorrefo.event_driven_social.post.adapter.outbound.outbox

import io.github.snorrefo.event_driven_social.shared.port.EventPublisher
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxPublishJob(
    private val outboxRepository: OutboxRepository,
    private val eventPublisher: EventPublisher,
) : Job {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val entries = outboxRepository.findUnpublished()
        entries.forEach { entry ->
            try {
                eventPublisher.publish(entry)
                outboxRepository.markPublished(entry.id)
            } catch (e: Exception) {
                log.error("Failed to publish outbox entry ${entry.id}", e)
            }
        }
    }
}
