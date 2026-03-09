package io.github.snorrefo.event_driven_social.socialgraph.adapter.outbound.persistence.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.Instant
import java.util.*

data class FollowId(
    val followerId: UUID = UUID.randomUUID(),
    val followedId: UUID = UUID.randomUUID()
) : Serializable

@Entity
@Table(name = "follows")
@IdClass(FollowId::class)
class FollowJpaEntity(
    @Id
    @Column(nullable = false)
    val followerId: UUID,

    @Id
    @Column(nullable = false)
    val followedId: UUID,

    @Column(nullable = false)
    val followedAt: Instant
)
