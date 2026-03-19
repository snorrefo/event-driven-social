package io.github.snorrefo.event_driven_social.post.domain.port.outbound

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import java.util.UUID

interface PostRepository {
    fun save(post: Post): Post
    fun findById(id: UUID): Post?
    fun findByAuthorId(authorId: UUID): List<Post>
}
