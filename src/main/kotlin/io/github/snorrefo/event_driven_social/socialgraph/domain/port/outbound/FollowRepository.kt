package io.github.snorrefo.event_driven_social.socialgraph.domain.port.outbound

import io.github.snorrefo.event_driven_social.socialgraph.domain.model.Follow
import java.util.*

interface FollowRepository {
    fun save(follow: Follow): Follow
    fun delete(followerId: UUID, followedId: UUID)
    fun findFollowedUserIds(userId: UUID): List<UUID>
    fun exists(followerId: UUID, followedId: UUID): Boolean
}
