package io.github.snorrefo.event_driven_social.web.controller


import io.github.snorrefo.event_driven_social.domain.service.PostService
import io.github.snorrefo.event_driven_social.web.dto.request.CreatePostRequest
import io.github.snorrefo.event_driven_social.web.dto.response.PostResponse
import io.github.snorrefo.event_driven_social.web.dto.response.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postService: PostService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPost(
        @Valid @RequestBody request: CreatePostRequest,
        @RequestHeader("X-User-Id") userId: String
    ): PostResponse {
        val post = postService.createPost(
            authorId = UUID.fromString(userId),
            content = request.content,
            inReplyToPostId = request.inReplyToPostId?.let { UUID.fromString(it) },
            mediaUrls = request.mediaUrls
        )
        return post.toResponse()
    }

    @GetMapping("/{postId}")
    fun getPost(@PathVariable postId: UUID): PostResponse {
        val post = postService.getPost(postId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found")
        return post.toResponse()
    }

    @GetMapping("/user/{userId}")
    fun getUserPosts(
        @PathVariable userId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<PostResponse> {
        return postService.getUserPosts(userId, page, size)
            .map { it.toResponse() }
    }
}