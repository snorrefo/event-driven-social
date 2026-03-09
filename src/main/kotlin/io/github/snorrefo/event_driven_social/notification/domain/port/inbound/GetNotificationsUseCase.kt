package io.github.snorrefo.event_driven_social.notification.domain.port.inbound

import io.github.snorrefo.event_driven_social.notification.domain.model.Notification
import java.util.*

interface GetNotificationsUseCase {
    fun getNotifications(userId: UUID, page: Int = 0, size: Int = 20): List<Notification>
    fun getUnreadCount(userId: UUID): Long
}
