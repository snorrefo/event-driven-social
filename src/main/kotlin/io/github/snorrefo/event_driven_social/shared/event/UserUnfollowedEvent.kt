package io.github.snorrefo.event_driven_social.shared.event

import java.time.Instant
import java.util.*

data class UserUnfollowedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val timestamp: Instant = Instant.now(),
    override val version: String = "1.0",
    val data: UserUnfollowedData
) : DomainEvent()

data class UserUnfollowedData(
    val followerId: String,
    val unfollowedUserId: String,
    val unfollowedAt: Instant
)
