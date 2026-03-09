package io.github.snorrefo.event_driven_social.socialgraph.domain.port.inbound

import java.util.*

interface GetFollowersUseCase {
    fun getFollowerIds(userId: UUID): List<UUID>
}
