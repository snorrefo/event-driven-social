package io.github.snorrefo.event_driven_social.timeline.domain.port.outbound

import java.util.UUID

interface FollowerProjectionRepository {
    fun addFollower(followedId: UUID, followerId: UUID)
    fun removeFollower(followedId: UUID, followerId: UUID)
    fun getFollowers(userId: UUID): Set<UUID>
}
