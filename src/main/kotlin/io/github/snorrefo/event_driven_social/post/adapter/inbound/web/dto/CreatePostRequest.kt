package io.github.snorrefo.event_driven_social.post.adapter.inbound.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreatePostRequest(
    @field:NotBlank(message = "Content cannot be blank")
    @field:Size(max = 280, message = "Content must be 280 characters or less")
    val content: String,

    val inReplyToPostId: String? = null,

    val mediaUrls: List<String> = emptyList()
)
