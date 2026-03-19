package io.github.snorrefo.event_driven_social.user.domain.port.inbound

import java.util.UUID

interface GetFollowersUseCase {
    fun getFollowers(userId: UUID): List<UUID>
}
