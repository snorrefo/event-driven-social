package io.github.snorrefo.event_driven_social.notification.domain.port.outbound

import io.github.snorrefo.event_driven_social.notification.domain.model.Notification
import java.util.*

interface NotificationRepository {
    fun save(notification: Notification): Notification
    fun findByUserId(userId: UUID, page: Int, size: Int): List<Notification>
    fun countUnreadByUserId(userId: UUID): Long
    fun markRead(notificationId: UUID)
    fun markAllReadByUserId(userId: UUID)
}
