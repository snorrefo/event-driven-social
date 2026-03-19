package io.github.snorrefo.event_driven_social.post.adapter.inbound.web

import io.github.snorrefo.event_driven_social.post.adapter.inbound.web.dto.CreatePostRequest
import io.github.snorrefo.event_driven_social.post.adapter.inbound.web.dto.PostResponse
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.CreatePostUseCase
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.GetPostUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val createPostUseCase: CreatePostUseCase,
    private val getPostUseCase: GetPostUseCase,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPost(@Valid @RequestBody request: CreatePostRequest): PostResponse {
        val post = createPostUseCase.createPost(request.authorId, request.content)
        return PostResponse.from(post)
    }

    @GetMapping("/{id}")
    fun getPost(@PathVariable id: UUID): PostResponse {
        val post = getPostUseCase.getPost(id)
        return PostResponse.from(post)
    }

    @GetMapping
    fun getPostsByAuthor(@RequestParam authorId: UUID): List<PostResponse> =
        getPostUseCase.getPostsByAuthor(authorId).map { PostResponse.from(it) }
}
