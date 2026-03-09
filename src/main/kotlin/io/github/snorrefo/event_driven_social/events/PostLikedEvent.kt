package io.github.snorrefo.event_driven_social.events


import java.time.Instant
import java.util.*

data class PostLikedEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val timestamp: Instant = Instant.now(),
    override val version: String = "1.0",
    val data: PostLikedData
) : DomainEvent()

data class PostLikedData(
    val postId: String,
    val userId: String,
    val likedAt: Instant
)