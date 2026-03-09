package io.github.snorrefo.event_driven_social.notification.adapter.outbound.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "notifications")
class NotificationJpaEntity(
    @Id
    val id: UUID,

    @Column(nullable = false)
    val userId: UUID,

    @Column(nullable = false, length = 50)
    val type: String,

    @Column(nullable = false)
    val actorId: UUID,

    @Column
    val referenceId: UUID? = null,

    @Column(nullable = false)
    var read: Boolean = false,

    @Column(nullable = false)
    val createdAt: Instant
)
