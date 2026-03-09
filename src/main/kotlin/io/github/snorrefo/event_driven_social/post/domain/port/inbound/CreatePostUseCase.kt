package io.github.snorrefo.event_driven_social.post.domain.port.inbound

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import java.util.*

interface CreatePostUseCase {
    fun createPost(
        authorId: UUID,
        content: String,
        inReplyToPostId: UUID? = null,
        mediaUrls: List<String> = emptyList()
    ): Post
}
