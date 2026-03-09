package io.github.snorrefo.event_driven_social.web.controller


import io.github.snorrefo.event_driven_social.domain.service.UserService
import io.github.snorrefo.event_driven_social.web.dto.request.CreateUserRequest
import io.github.snorrefo.event_driven_social.web.dto.response.UserResponse
import io.github.snorrefo.event_driven_social.web.dto.response.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody request: CreateUserRequest): UserResponse {
        val user = userService.createUser(
            username = request.username,
            displayName = request.displayName,
            bio = request.bio,
            avatarUrl = request.avatarUrl
        )
        return user.toResponse()
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserResponse {
        val user = userService.getUser(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return user.toResponse()
    }

    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): UserResponse {
        val user = userService.getUserByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return user.toResponse()
    }
}