package io.github.snorrefo.event_driven_social.notification.adapter.outbound.persistence.repository

import io.github.snorrefo.event_driven_social.notification.adapter.outbound.persistence.entity.NotificationJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NotificationJpaRepository : JpaRepository<NotificationJpaEntity, UUID> {

    fun findByUserIdOrderByCreatedAtDesc(userId: UUID, pageable: Pageable): List<NotificationJpaEntity>

    fun countByUserIdAndReadFalse(userId: UUID): Long

    @Modifying
    @Query("UPDATE NotificationJpaEntity n SET n.read = true WHERE n.id = :notificationId")
    fun markRead(@Param("notificationId") notificationId: UUID)

    @Modifying
    @Query("UPDATE NotificationJpaEntity n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    fun markAllReadByUserId(@Param("userId") userId: UUID)
}
