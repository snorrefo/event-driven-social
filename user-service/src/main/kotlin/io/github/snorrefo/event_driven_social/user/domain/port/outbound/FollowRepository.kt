package io.github.snorrefo.event_driven_social.user.domain.port.outbound

import io.github.snorrefo.event_driven_social.user.domain.model.Follow
import java.util.UUID

interface FollowRepository {
    fun save(follow: Follow): Follow
    fun delete(followerId: UUID, followedId: UUID)
    fun exists(followerId: UUID, followedId: UUID): Boolean
    fun findFollowers(userId: UUID): List<UUID>
    fun findFollowedUsers(userId: UUID): List<UUID>
}
