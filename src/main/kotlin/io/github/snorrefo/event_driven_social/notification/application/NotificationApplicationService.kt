package io.github.snorrefo.event_driven_social.notification.application

import io.github.snorrefo.event_driven_social.notification.domain.model.Notification
import io.github.snorrefo.event_driven_social.notification.domain.model.NotificationType
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.CreateNotificationUseCase
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.GetNotificationsUseCase
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.MarkNotificationReadUseCase
import io.github.snorrefo.event_driven_social.notification.domain.port.outbound.NotificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class NotificationApplicationService(
    private val notificationRepository: NotificationRepository
) : CreateNotificationUseCase, GetNotificationsUseCase, MarkNotificationReadUseCase {

    @Transactional
    override fun createNotification(userId: UUID, type: NotificationType, actorId: UUID, referenceId: UUID?): Notification {
        val notification = Notification(
            userId = userId,
            type = type,
            actorId = actorId,
            referenceId = referenceId
        )
        return notificationRepository.save(notification)
    }

    override fun getNotifications(userId: UUID, page: Int, size: Int): List<Notification> =
        notificationRepository.findByUserId(userId, page, size)

    override fun getUnreadCount(userId: UUID): Long =
        notificationRepository.countUnreadByUserId(userId)

    @Transactional
    override fun markRead(notificationId: UUID) =
        notificationRepository.markRead(notificationId)

    @Transactional
    override fun markAllRead(userId: UUID) =
        notificationRepository.markAllReadByUserId(userId)
}
