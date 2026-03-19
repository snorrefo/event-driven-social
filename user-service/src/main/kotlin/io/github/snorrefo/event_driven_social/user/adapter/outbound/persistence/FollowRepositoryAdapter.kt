package io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.entity.FollowJpaEntity
import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.repository.FollowJpaRepository
import io.github.snorrefo.event_driven_social.user.domain.model.Follow
import io.github.snorrefo.event_driven_social.user.domain.port.outbound.FollowRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class FollowRepositoryAdapter(
    private val jpaRepository: FollowJpaRepository,
) : FollowRepository {

    override fun save(follow: Follow): Follow =
        jpaRepository.save(FollowJpaEntity.fromDomain(follow)).toDomain()

    override fun delete(followerId: UUID, followedId: UUID) {
        val entity = jpaRepository.findByFollowerIdAndFollowedId(followerId, followedId)
        if (entity != null) jpaRepository.delete(entity)
    }

    override fun exists(followerId: UUID, followedId: UUID): Boolean =
        jpaRepository.existsByFollowerIdAndFollowedId(followerId, followedId)

    override fun findFollowers(userId: UUID): List<UUID> =
        jpaRepository.findByFollowedId(userId).map { it.followerId }

    override fun findFollowedUsers(userId: UUID): List<UUID> =
        jpaRepository.findByFollowerId(userId).map { it.followedId }
}
