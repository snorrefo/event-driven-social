package io.github.snorrefo.event_driven_social.notification.domain.port.inbound

import java.util.*

interface MarkNotificationReadUseCase {
    fun markRead(notificationId: UUID)
    fun markAllRead(userId: UUID)
}
