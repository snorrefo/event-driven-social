package io.github.snorrefo.event_driven_social.web.dto.response


import io.github.snorrefo.event_driven_social.domain.model.Post
import java.time.Instant

data class PostResponse(
    val id: String,
    val authorId: String,
    val content: String,
    val createdAt: Instant,
    val inReplyToPostId: String? = null,
    val mediaUrls: List<String> = emptyList()
)

fun Post.toResponse() = PostResponse(
    id = id.toString(),
    authorId = authorId.toString(),
    content = content,
    createdAt = createdAt,
    inReplyToPostId = inReplyToPostId?.toString(),
    mediaUrls = mediaUrls
)