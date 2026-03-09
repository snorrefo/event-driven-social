package io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id
    val id: UUID,

    @Column(unique = true, nullable = false, length = 50)
    val username: String,

    @Column(nullable = false, length = 100)
    val displayName: String,

    @Column(length = 160)
    val bio: String? = null,

    @Column
    val avatarUrl: String? = null,

    @Column(nullable = false)
    val createdAt: Instant
)
