package io.github.snorrefo.event_driven_social.identity.adapter.inbound.web

import io.github.snorrefo.event_driven_social.identity.adapter.inbound.web.dto.CreateUserRequest
import io.github.snorrefo.event_driven_social.identity.adapter.inbound.web.dto.UserResponse
import io.github.snorrefo.event_driven_social.identity.adapter.inbound.web.dto.toResponse
import io.github.snorrefo.event_driven_social.identity.domain.port.inbound.CreateUserUseCase
import io.github.snorrefo.event_driven_social.identity.domain.port.inbound.GetUserUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody request: CreateUserRequest): UserResponse {
        val user = createUserUseCase.createUser(
            username = request.username,
            displayName = request.displayName,
            bio = request.bio,
            avatarUrl = request.avatarUrl
        )
        return user.toResponse()
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserResponse {
        val user = getUserUseCase.getUser(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return user.toResponse()
    }

    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): UserResponse {
        val user = getUserUseCase.getUserByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return user.toResponse()
    }
}
