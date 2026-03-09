package io.github.snorrefo.event_driven_social.notification.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.notification.adapter.outbound.persistence.repository.NotificationJpaRepository
import io.github.snorrefo.event_driven_social.notification.domain.model.Notification
import io.github.snorrefo.event_driven_social.notification.domain.model.NotificationType
import io.github.snorrefo.event_driven_social.shared.TestContainersConfiguration
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private lateinit var notificationJpaRepository: NotificationJpaRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    private val notificationRepository by lazy { NotificationRepositoryAdapter(notificationJpaRepository) }

    @Test
    fun `should save and retrieve notification`() {
        val notification = Notification(
            userId = UUID.randomUUID(),
            type = NotificationType.NEW_FOLLOWER,
            actorId = UUID.randomUUID()
        )

        val saved = notificationRepository.save(notification)

        assertNotNull(saved)
        assertEquals(notification.userId, saved.userId)
        assertEquals(NotificationType.NEW_FOLLOWER, saved.type)
    }

    @Test
    fun `should find notifications by user id`() {
        val userId = UUID.randomUUID()

        notificationRepository.save(Notification(userId = userId, type = NotificationType.NEW_FOLLOWER, actorId = UUID.randomUUID()))
        notificationRepository.save(Notification(userId = userId, type = NotificationType.NEW_POST, actorId = UUID.randomUUID(), referenceId = UUID.randomUUID()))
        notificationRepository.save(Notification(userId = UUID.randomUUID(), type = NotificationType.NEW_FOLLOWER, actorId = UUID.randomUUID()))

        val result = notificationRepository.findByUserId(userId, 0, 20)

        assertEquals(2, result.size)
    }

    @Test
    fun `should count unread notifications`() {
        val userId = UUID.randomUUID()

        notificationRepository.save(Notification(userId = userId, type = NotificationType.NEW_FOLLOWER, actorId = UUID.randomUUID()))
        notificationRepository.save(Notification(userId = userId, type = NotificationType.NEW_POST, actorId = UUID.randomUUID(), referenceId = UUID.randomUUID()))

        val count = notificationRepository.countUnreadByUserId(userId)

        assertEquals(2, count)
    }

    @Test
    fun `should mark notification as read`() {
        val notification = notificationRepository.save(
            Notification(userId = UUID.randomUUID(), type = NotificationType.NEW_FOLLOWER, actorId = UUID.randomUUID())
        )

        notificationRepository.markRead(notification.id)
        notificationJpaRepository.flush()
        entityManager.clear()

        val updated = notificationJpaRepository.findById(notification.id).orElse(null)
        assertNotNull(updated)
        assertTrue(updated.read)
    }

    @Test
    fun `should mark all notifications as read for user`() {
        val userId = UUID.randomUUID()

        notificationRepository.save(Notification(userId = userId, type = NotificationType.NEW_FOLLOWER, actorId = UUID.randomUUID()))
        notificationRepository.save(Notification(userId = userId, type = NotificationType.NEW_POST, actorId = UUID.randomUUID(), referenceId = UUID.randomUUID()))

        notificationRepository.markAllReadByUserId(userId)
        notificationJpaRepository.flush()

        val count = notificationRepository.countUnreadByUserId(userId)
        assertEquals(0, count)
    }
}
