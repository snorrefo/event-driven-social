package io.github.snorrefo.event_driven_social.user.domain.port.inbound

import java.util.UUID

interface FollowUserUseCase {
    fun follow(followerId: UUID, followedId: UUID)
}
