package io.github.snorrefo.event_driven_social.user.domain.port.inbound

import java.util.UUID

interface UnfollowUserUseCase {
    fun unfollow(followerId: UUID, followedId: UUID)
}
