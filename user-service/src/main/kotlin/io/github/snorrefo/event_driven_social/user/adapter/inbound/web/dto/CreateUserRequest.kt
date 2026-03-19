package io.github.snorrefo.event_driven_social.user.adapter.inbound.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUserRequest(
    @field:NotBlank @field:Size(min = 3, max = 30) val username: String,
    @field:NotBlank @field:Size(max = 50) val displayName: String,
)
