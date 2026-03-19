package io.github.snorrefo.event_driven_social.user.adapter.inbound.web

import io.github.snorrefo.event_driven_social.user.adapter.inbound.web.dto.CreateUserRequest
import io.github.snorrefo.event_driven_social.user.adapter.inbound.web.dto.UserResponse
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.CreateUserUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.GetUserUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@Valid @RequestBody request: CreateUserRequest): UserResponse {
        val user = createUserUseCase.createUser(request.username, request.displayName)
        return UserResponse.from(user)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: UUID): UserResponse {
        val user = getUserUseCase.getUser(id)
        return UserResponse.from(user)
    }
}
