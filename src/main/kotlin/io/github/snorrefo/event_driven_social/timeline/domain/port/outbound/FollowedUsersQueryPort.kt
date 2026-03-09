package io.github.snorrefo.event_driven_social.timeline.domain.port.outbound

import java.util.*

interface FollowedUsersQueryPort {
    fun getFollowedUserIds(userId: UUID): List<UUID>
}
