package io.github.snorrefo.event_driven_social.shared.event

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.Instant
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes(
    JsonSubTypes.Type(value = PostCreatedEvent::class, name = "post.created"),
    JsonSubTypes.Type(value = UserFollowedEvent::class, name = "user.followed"),
    JsonSubTypes.Type(value = UserUnfollowedEvent::class, name = "user.unfollowed"),
)
sealed class DomainEvent {
    abstract val eventId: UUID
    abstract val occurredAt: Instant
    abstract val eventType: String
}

data class PostCreatedEvent @JsonCreator constructor(
    @JsonProperty("eventId") override val eventId: UUID = UUID.randomUUID(),
    @JsonProperty("occurredAt") override val occurredAt: Instant = Instant.now(),
    @JsonProperty("postId") val postId: UUID,
    @JsonProperty("authorId") val authorId: UUID,
    @JsonProperty("content") val content: String,
    @JsonProperty("eventType") override val eventType: String = "post.created",
) : DomainEvent()

data class UserFollowedEvent @JsonCreator constructor(
    @JsonProperty("eventId") override val eventId: UUID = UUID.randomUUID(),
    @JsonProperty("occurredAt") override val occurredAt: Instant = Instant.now(),
    @JsonProperty("followerId") val followerId: UUID,
    @JsonProperty("followedId") val followedId: UUID,
    @JsonProperty("eventType") override val eventType: String = "user.followed",
) : DomainEvent()

data class UserUnfollowedEvent @JsonCreator constructor(
    @JsonProperty("eventId") override val eventId: UUID = UUID.randomUUID(),
    @JsonProperty("occurredAt") override val occurredAt: Instant = Instant.now(),
    @JsonProperty("followerId") val followerId: UUID,
    @JsonProperty("followedId") val followedId: UUID,
    @JsonProperty("eventType") override val eventType: String = "user.unfollowed",
) : DomainEvent()
