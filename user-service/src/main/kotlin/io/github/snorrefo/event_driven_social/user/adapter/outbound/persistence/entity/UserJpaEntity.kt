package io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.entity

import io.github.snorrefo.event_driven_social.user.domain.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column(unique = true, nullable = false) val username: String = "",
    @Column(nullable = false) val displayName: String = "",
    @Column(nullable = false) val createdAt: Instant = Instant.now(),
) {
    fun toDomain() = User(id = id, username = username, displayName = displayName, createdAt = createdAt)

    companion object {
        fun fromDomain(user: User) = UserJpaEntity(
            id = user.id, username = user.username, displayName = user.displayName, createdAt = user.createdAt,
        )
    }
}
