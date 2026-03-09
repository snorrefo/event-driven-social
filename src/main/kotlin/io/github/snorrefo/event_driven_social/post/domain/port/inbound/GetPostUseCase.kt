package io.github.snorrefo.event_driven_social.post.domain.port.inbound

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import java.util.*

interface GetPostUseCase {
    fun getPost(postId: UUID): Post?
    fun getUserPosts(authorId: UUID, page: Int = 0, size: Int = 20): List<Post>
    fun getPostCount(authorId: UUID): Long
}
