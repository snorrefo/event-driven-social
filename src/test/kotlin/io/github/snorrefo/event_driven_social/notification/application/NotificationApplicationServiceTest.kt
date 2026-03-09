package io.github.snorrefo.event_driven_social.notification.application

import io.github.snorrefo.event_driven_social.notification.domain.model.Notification
import io.github.snorrefo.event_driven_social.notification.domain.model.NotificationType
import io.github.snorrefo.event_driven_social.notification.domain.port.outbound.NotificationRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NotificationApplicationServiceTest {

    private val notificationRepository = mock<NotificationRepository>()
    private val service = NotificationApplicationService(notificationRepository)

    @Test
    fun `should create notification`() {
        val userId = UUID.randomUUID()
        val actorId = UUID.randomUUID()

        whenever(notificationRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = service.createNotification(userId, NotificationType.NEW_FOLLOWER, actorId)

        assertNotNull(result)
        assertEquals(userId, result.userId)
        assertEquals(NotificationType.NEW_FOLLOWER, result.type)
        assertEquals(actorId, result.actorId)
        verify(notificationRepository).save(any())
    }

    @Test
    fun `should create notification with reference id`() {
        val userId = UUID.randomUUID()
        val actorId = UUID.randomUUID()
        val referenceId = UUID.randomUUID()

        whenever(notificationRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = service.createNotification(userId, NotificationType.NEW_POST, actorId, referenceId)

        assertEquals(referenceId, result.referenceId)
    }

    @Test
    fun `should get notifications`() {
        val userId = UUID.randomUUID()
        val notifications = listOf(
            Notification(userId = userId, type = NotificationType.NEW_FOLLOWER, actorId = UUID.randomUUID()),
            Notification(userId = userId, type = NotificationType.NEW_POST, actorId = UUID.randomUUID(), referenceId = UUID.randomUUID())
        )

        whenever(notificationRepository.findByUserId(userId, 0, 20)).thenReturn(notifications)

        val result = service.getNotifications(userId)

        assertEquals(2, result.size)
    }

    @Test
    fun `should get unread count`() {
        val userId = UUID.randomUUID()

        whenever(notificationRepository.countUnreadByUserId(userId)).thenReturn(5)

        val result = service.getUnreadCount(userId)

        assertEquals(5, result)
    }

    @Test
    fun `should mark notification as read`() {
        val notificationId = UUID.randomUUID()

        service.markRead(notificationId)

        verify(notificationRepository).markRead(notificationId)
    }

    @Test
    fun `should mark all notifications as read`() {
        val userId = UUID.randomUUID()

        service.markAllRead(userId)

        verify(notificationRepository).markAllReadByUserId(userId)
    }
}
