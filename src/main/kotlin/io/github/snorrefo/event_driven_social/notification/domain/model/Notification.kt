package io.github.snorrefo.event_driven_social.notification.domain.model

import java.time.Instant
import java.util.*

data class Notification(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val type: NotificationType,
    val actorId: UUID,
    val referenceId: UUID? = null,
    val read: Boolean = false,
    val createdAt: Instant = Instant.now()
)
