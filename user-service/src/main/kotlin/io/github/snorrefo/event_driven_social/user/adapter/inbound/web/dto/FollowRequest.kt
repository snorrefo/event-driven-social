package io.github.snorrefo.event_driven_social.user.adapter.inbound.web.dto

import java.util.UUID

data class FollowRequest(
    val followedId: UUID,
)
