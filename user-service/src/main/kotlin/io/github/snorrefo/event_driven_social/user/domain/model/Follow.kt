package io.github.snorrefo.event_driven_social.user.domain.model

import java.time.Instant
import java.util.UUID

data class Follow(
    val id: UUID = UUID.randomUUID(),
    val followerId: UUID,
    val followedId: UUID,
    val createdAt: Instant = Instant.now(),
)
