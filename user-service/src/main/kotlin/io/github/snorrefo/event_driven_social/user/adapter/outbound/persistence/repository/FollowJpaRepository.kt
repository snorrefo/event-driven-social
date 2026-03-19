package io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.repository

import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.entity.FollowJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FollowJpaRepository : JpaRepository<FollowJpaEntity, UUID> {
    fun findByFollowerIdAndFollowedId(followerId: UUID, followedId: UUID): FollowJpaEntity?
    fun existsByFollowerIdAndFollowedId(followerId: UUID, followedId: UUID): Boolean
    fun findByFollowedId(followedId: UUID): List<FollowJpaEntity>
    fun findByFollowerId(followerId: UUID): List<FollowJpaEntity>
}
