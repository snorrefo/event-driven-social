package io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.entity

import io.github.snorrefo.event_driven_social.user.domain.model.Follow
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "follows")
class FollowJpaEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column(nullable = false) val followerId: UUID = UUID.randomUUID(),
    @Column(nullable = false) val followedId: UUID = UUID.randomUUID(),
    @Column(nullable = false) val createdAt: Instant = Instant.now(),
) {
    fun toDomain() = Follow(id = id, followerId = followerId, followedId = followedId, createdAt = createdAt)

    companion object {
        fun fromDomain(follow: Follow) = FollowJpaEntity(
            id = follow.id, followerId = follow.followerId, followedId = follow.followedId, createdAt = follow.createdAt,
        )
    }
}
