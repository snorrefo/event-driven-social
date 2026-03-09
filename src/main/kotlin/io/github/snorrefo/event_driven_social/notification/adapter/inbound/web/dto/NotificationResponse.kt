package io.github.snorrefo.event_driven_social.notification.adapter.inbound.web.dto

import io.github.snorrefo.event_driven_social.notification.domain.model.Notification
import java.time.Instant

data class NotificationResponse(
    val id: String,
    val userId: String,
    val type: String,
    val actorId: String,
    val referenceId: String?,
    val read: Boolean,
    val createdAt: Instant
)

fun Notification.toResponse() = NotificationResponse(
    id = id.toString(),
    userId = userId.toString(),
    type = type.name,
    actorId = actorId.toString(),
    referenceId = referenceId?.toString(),
    read = read,
    createdAt = createdAt
)
