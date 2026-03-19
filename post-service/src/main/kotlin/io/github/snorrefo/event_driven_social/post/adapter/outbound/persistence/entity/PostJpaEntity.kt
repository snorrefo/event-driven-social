package io.github.snorrefo.event_driven_social.post.adapter.outbound.persistence.entity

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "posts")
class PostJpaEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column(nullable = false) val authorId: UUID = UUID.randomUUID(),
    @Column(nullable = false, length = 280) val content: String = "",
    @Column(nullable = false) val createdAt: Instant = Instant.now(),
) {
    fun toDomain() = Post(id = id, authorId = authorId, content = content, createdAt = createdAt)

    companion object {
        fun fromDomain(post: Post) = PostJpaEntity(
            id = post.id, authorId = post.authorId, content = post.content, createdAt = post.createdAt,
        )
    }
}
