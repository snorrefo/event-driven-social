package io.github.snorrefo.event_driven_social.web.dto.request


import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val username: String,

    @field:NotBlank
    @field:Size(max = 100)
    val displayName: String,

    @field:Size(max = 160)
    val bio: String? = null,

    val avatarUrl: String? = null
)