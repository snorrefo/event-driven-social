package io.github.snorrefo.event_driven_social.shared.event

import java.time.Instant
import java.util.*

data class UserFollowedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val timestamp: Instant = Instant.now(),
    override val version: String = "1.0",
    val data: UserFollowedData
) : DomainEvent()

data class UserFollowedData(
    val followerId: String,
    val followedUserId: String,
    val followedAt: Instant
)
