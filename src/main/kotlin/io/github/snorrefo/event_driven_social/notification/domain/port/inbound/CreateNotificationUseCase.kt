package io.github.snorrefo.event_driven_social.notification.domain.port.inbound

import io.github.snorrefo.event_driven_social.notification.domain.model.Notification
import io.github.snorrefo.event_driven_social.notification.domain.model.NotificationType
import java.util.*

interface CreateNotificationUseCase {
    fun createNotification(userId: UUID, type: NotificationType, actorId: UUID, referenceId: UUID? = null): Notification
}
