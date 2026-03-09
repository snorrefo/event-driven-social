package io.github.snorrefo.event_driven_social.domain.model


import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "posts")
data class Post(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val authorId: UUID,

    @Column(nullable = false, length = 280)
    val content: String,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column
    val inReplyToPostId: UUID? = null,

    @ElementCollection
    @CollectionTable(name = "post_media", joinColumns = [JoinColumn(name = "post_id")])
    @Column(name = "media_url", nullable = false)
    val mediaUrls: List<String> = emptyList()
)