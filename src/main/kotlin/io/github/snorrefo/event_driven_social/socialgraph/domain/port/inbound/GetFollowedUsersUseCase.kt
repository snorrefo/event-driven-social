package io.github.snorrefo.event_driven_social.socialgraph.domain.port.inbound

import java.util.*

interface GetFollowedUsersUseCase {
    fun getFollowedUserIds(userId: UUID): List<UUID>
}
