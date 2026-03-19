package io.github.snorrefo.event_driven_social.post.adapter.inbound.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreatePostRequest(
    val authorId: UUID,
    @field:NotBlank @field:Size(max = 280) val content: String,
)
