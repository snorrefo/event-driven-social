package io.github.snorrefo.event_driven_social.domain.model


import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "outbox_events", indexes = [
        Index(name = "idx_outbox_published_at", columnList = "publishedAt")
    ]
)
data class OutboxEvent(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 50)
    val aggregateType: String,

    @Column(nullable = false)
    val aggregateId: UUID,

    @Column(nullable = false, length = 100)
    val eventType: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val payload: String,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column
    var publishedAt: Instant? = null
)