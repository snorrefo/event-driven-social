package io.github.snorrefo.event_driven_social.notification.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.notification.adapter.outbound.persistence.entity.NotificationJpaEntity
import io.github.snorrefo.event_driven_social.notification.adapter.outbound.persistence.repository.NotificationJpaRepository
import io.github.snorrefo.event_driven_social.notification.domain.model.Notification
import io.github.snorrefo.event_driven_social.notification.domain.model.NotificationType
import io.github.snorrefo.event_driven_social.notification.domain.port.outbound.NotificationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class NotificationRepositoryAdapter(
    private val jpaRepository: NotificationJpaRepository
) : NotificationRepository {

    override fun save(notification: Notification): Notification =
        jpaRepository.save(notification.toJpaEntity()).toDomain()

    override fun findByUserId(userId: UUID, page: Int, size: Int): List<Notification> =
        jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
            .map { it.toDomain() }

    override fun countUnreadByUserId(userId: UUID): Long =
        jpaRepository.countByUserIdAndReadFalse(userId)

    override fun markRead(notificationId: UUID) =
        jpaRepository.markRead(notificationId)

    override fun markAllReadByUserId(userId: UUID) =
        jpaRepository.markAllReadByUserId(userId)
}

private fun Notification.toJpaEntity() = NotificationJpaEntity(
    id = id,
    userId = userId,
    type = type.name,
    actorId = actorId,
    referenceId = referenceId,
    read = read,
    createdAt = createdAt
)

private fun NotificationJpaEntity.toDomain() = Notification(
    id = id,
    userId = userId,
    type = NotificationType.valueOf(type),
    actorId = actorId,
    referenceId = referenceId,
    read = read,
    createdAt = createdAt
)
