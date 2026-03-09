package io.github.snorrefo.event_driven_social.socialgraph.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.socialgraph.adapter.outbound.persistence.entity.FollowJpaEntity
import io.github.snorrefo.event_driven_social.socialgraph.adapter.outbound.persistence.repository.FollowJpaRepository
import io.github.snorrefo.event_driven_social.socialgraph.domain.model.Follow
import io.github.snorrefo.event_driven_social.socialgraph.domain.port.outbound.FollowRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class FollowRepositoryAdapter(
    private val jpaRepository: FollowJpaRepository
) : FollowRepository {

    override fun save(follow: Follow): Follow =
        jpaRepository.save(follow.toJpaEntity()).toDomain()

    override fun delete(followerId: UUID, followedId: UUID) =
        jpaRepository.deleteByFollowerIdAndFollowedId(followerId, followedId)

    override fun findFollowedUserIds(userId: UUID): List<UUID> =
        jpaRepository.findFollowedIdsByFollowerId(userId)

    override fun exists(followerId: UUID, followedId: UUID): Boolean =
        jpaRepository.existsByFollowerIdAndFollowedId(followerId, followedId)
}

private fun Follow.toJpaEntity() = FollowJpaEntity(
    followerId = followerId,
    followedId = followedId,
    followedAt = followedAt
)

private fun FollowJpaEntity.toDomain() = Follow(
    followerId = followerId,
    followedId = followedId,
    followedAt = followedAt
)
