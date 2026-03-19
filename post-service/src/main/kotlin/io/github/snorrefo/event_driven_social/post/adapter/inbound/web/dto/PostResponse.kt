package io.github.snorrefo.event_driven_social.post.adapter.inbound.web.dto

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import java.time.Instant
import java.util.UUID

data class PostResponse(
    val id: UUID,
    val authorId: UUID,
    val content: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(post: Post) = PostResponse(
            id = post.id, authorId = post.authorId, content = post.content, createdAt = post.createdAt,
        )
    }
}
