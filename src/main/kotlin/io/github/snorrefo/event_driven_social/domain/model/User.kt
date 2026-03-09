package io.github.snorrefo.event_driven_social.domain.model


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false, length = 50)
    val username: String,

    @Column(nullable = false, length = 100)
    val displayName: String,

    @Column(length = 160)
    val bio: String? = null,

    @Column
    val avatarUrl: String? = null,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
)