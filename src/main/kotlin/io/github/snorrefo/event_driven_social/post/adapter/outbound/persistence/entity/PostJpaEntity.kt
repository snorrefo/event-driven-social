package io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "posts")
class PostJpaEntity(
    @Id
    val id: UUID,

    @Column(nullable = false)
    val authorId: UUID,

    @Column(nullable = false, length = 280)
    val content: String,

    @Column(nullable = false)
    val createdAt: Instant,

    @Column
    val inReplyToPostId: UUID? = null,

    @ElementCollection
    @CollectionTable(name = "post_media", joinColumns = [JoinColumn(name = "post_id")])
    @Column(name = "media_url", nullable = false)
    val mediaUrls: List<String> = emptyList()
)
