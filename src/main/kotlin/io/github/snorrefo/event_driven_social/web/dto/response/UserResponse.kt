package io.github.snorrefo.event_driven_social.web.dto.response


import io.github.snorrefo.event_driven_social.domain.model.User
import java.time.Instant

data class UserResponse(
    val id: String,
    val username: String,
    val displayName: String,
    val bio: String?,
    val avatarUrl: String?,
    val createdAt: Instant
)

fun User.toResponse() = UserResponse(
    id = id.toString(),
    username = username,
    displayName = displayName,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)