package io.github.snorrefo.event_driven_social.socialgraph.domain.port.inbound

import io.github.snorrefo.event_driven_social.socialgraph.domain.model.Follow
import java.util.*

interface FollowUserUseCase {
    fun follow(followerId: UUID, followedId: UUID): Follow
    fun unfollow(followerId: UUID, followedId: UUID)
}
