package io.github.snorrefo.event_driven_social.post.domain.port.inbound

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import java.util.UUID

interface CreatePostUseCase {
    fun createPost(authorId: UUID, content: String): Post
}
