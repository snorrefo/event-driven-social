package io.github.snorrefo.event_driven_social.notification.adapter.inbound.web

import io.github.snorrefo.event_driven_social.notification.domain.model.Notification
import io.github.snorrefo.event_driven_social.notification.domain.model.NotificationType
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.CreateNotificationUseCase
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.GetNotificationsUseCase
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.MarkNotificationReadUseCase
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getNotificationsUseCase: GetNotificationsUseCase

    @MockitoBean
    private lateinit var markNotificationReadUseCase: MarkNotificationReadUseCase

    @MockitoBean
    private lateinit var createNotificationUseCase: CreateNotificationUseCase

    @Test
    fun `should get notifications`() {
        val userId = UUID.randomUUID()
        val notifications = listOf(
            Notification(userId = userId, type = NotificationType.NEW_FOLLOWER, actorId = UUID.randomUUID())
        )

        `when`(getNotificationsUseCase.getNotifications(userId, 0, 20)).thenReturn(notifications)

        mockMvc.perform(
            get("/api/notifications")
                .header("X-User-Id", userId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].type").value("NEW_FOLLOWER"))
            .andExpect(jsonPath("$[0].read").value(false))
    }

    @Test
    fun `should get unread count`() {
        val userId = UUID.randomUUID()

        `when`(getNotificationsUseCase.getUnreadCount(userId)).thenReturn(3)

        mockMvc.perform(
            get("/api/notifications/unread-count")
                .header("X-User-Id", userId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.count").value(3))
    }

    @Test
    fun `should mark notification as read`() {
        val notificationId = UUID.randomUUID()

        mockMvc.perform(
            post("/api/notifications/$notificationId/read")
        )
            .andExpect(status().isNoContent)

        verify(markNotificationReadUseCase).markRead(notificationId)
    }

    @Test
    fun `should mark all notifications as read`() {
        val userId = UUID.randomUUID()

        mockMvc.perform(
            post("/api/notifications/read-all")
                .header("X-User-Id", userId.toString())
        )
            .andExpect(status().isNoContent)

        verify(markNotificationReadUseCase).markAllRead(userId)
    }
}
