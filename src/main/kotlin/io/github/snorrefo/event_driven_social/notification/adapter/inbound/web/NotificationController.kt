package io.github.snorrefo.event_driven_social.notification.adapter.inbound.web

import io.github.snorrefo.event_driven_social.notification.adapter.inbound.web.dto.NotificationResponse
import io.github.snorrefo.event_driven_social.notification.adapter.inbound.web.dto.toResponse
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.GetNotificationsUseCase
import io.github.snorrefo.event_driven_social.notification.domain.port.inbound.MarkNotificationReadUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase
) {

    @GetMapping
    fun getNotifications(
        @RequestHeader("X-User-Id") userId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<NotificationResponse> =
        getNotificationsUseCase.getNotifications(userId, page, size).map { it.toResponse() }

    @GetMapping("/unread-count")
    fun getUnreadCount(@RequestHeader("X-User-Id") userId: UUID): Map<String, Long> =
        mapOf("count" to getNotificationsUseCase.getUnreadCount(userId))

    @PostMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markRead(@PathVariable notificationId: UUID) =
        markNotificationReadUseCase.markRead(notificationId)

    @PostMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markAllRead(@RequestHeader("X-User-Id") userId: UUID) =
        markNotificationReadUseCase.markAllRead(userId)
}
