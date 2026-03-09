package io.github.snorrefo.event_driven_social.post.domain.port.outbound

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import java.util.*

interface PostRepository {
    fun save(post: Post): Post
    fun findById(postId: UUID): Post?
    fun findByAuthorIdOrderByCreatedAtDesc(authorId: UUID, page: Int, size: Int): List<Post>
    fun findTimelineForUsers(authorIds: List<UUID>, page: Int, size: Int): List<Post>
    fun countByAuthorId(authorId: UUID): Long
}
