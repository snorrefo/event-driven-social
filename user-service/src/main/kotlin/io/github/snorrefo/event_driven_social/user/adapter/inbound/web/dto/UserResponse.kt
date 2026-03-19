package io.github.snorrefo.event_driven_social.user.adapter.inbound.web.dto

import io.github.snorrefo.event_driven_social.user.domain.model.User
import java.time.Instant
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val username: String,
    val displayName: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(user: User) = UserResponse(
            id = user.id, username = user.username, displayName = user.displayName, createdAt = user.createdAt,
        )
    }
}
