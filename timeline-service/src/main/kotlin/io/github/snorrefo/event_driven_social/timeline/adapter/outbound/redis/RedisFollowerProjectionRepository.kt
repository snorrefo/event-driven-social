package io.github.snorrefo.event_driven_social.timeline.adapter.outbound.redis

import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.FollowerProjectionRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class RedisFollowerProjectionRepository(
    private val redisTemplate: StringRedisTemplate,
) : FollowerProjectionRepository {

    override fun addFollower(followedId: UUID, followerId: UUID) {
        redisTemplate.opsForSet().add("followers:$followedId", followerId.toString())
    }

    override fun removeFollower(followedId: UUID, followerId: UUID) {
        redisTemplate.opsForSet().remove("followers:$followedId", followerId.toString())
    }

    override fun getFollowers(userId: UUID): Set<UUID> {
        val members = redisTemplate.opsForSet().members("followers:$userId") ?: emptySet()
        return members.map { UUID.fromString(it) }.toSet()
    }
}
