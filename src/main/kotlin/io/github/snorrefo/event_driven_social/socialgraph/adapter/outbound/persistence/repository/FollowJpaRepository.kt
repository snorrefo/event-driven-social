package io.github.snorrefo.event_driven_social.socialgraph.adapter.outbound.persistence.repository

import io.github.snorrefo.event_driven_social.socialgraph.adapter.outbound.persistence.entity.FollowId
import io.github.snorrefo.event_driven_social.socialgraph.adapter.outbound.persistence.entity.FollowJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FollowJpaRepository : JpaRepository<FollowJpaEntity, FollowId> {

    @Query("SELECT f.followedId FROM FollowJpaEntity f WHERE f.followerId = :followerId")
    fun findFollowedIdsByFollowerId(@Param("followerId") followerId: UUID): List<UUID>

    fun deleteByFollowerIdAndFollowedId(followerId: UUID, followedId: UUID)

    fun existsByFollowerIdAndFollowedId(followerId: UUID, followedId: UUID): Boolean
}
