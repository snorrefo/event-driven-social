package io.github.snorrefo.event_driven_social.timeline.adapter.outbound.socialgraph

import io.github.snorrefo.event_driven_social.socialgraph.domain.port.inbound.GetFollowedUsersUseCase
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.FollowedUsersQueryPort
import org.springframework.stereotype.Component
import java.util.*

@Component
class FollowedUsersQueryAdapter(
    private val getFollowedUsersUseCase: GetFollowedUsersUseCase
) : FollowedUsersQueryPort {

    override fun getFollowedUserIds(userId: UUID): List<UUID> =
        getFollowedUsersUseCase.getFollowedUserIds(userId)
}
