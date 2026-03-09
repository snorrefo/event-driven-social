package io.github.snorrefo.event_driven_social.identity.domain.model

import java.time.Instant
import java.util.*

data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val displayName: String,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val createdAt: Instant = Instant.now()
)
