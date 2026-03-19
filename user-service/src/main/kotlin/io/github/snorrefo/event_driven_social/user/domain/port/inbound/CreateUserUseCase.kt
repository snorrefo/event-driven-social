package io.github.snorrefo.event_driven_social.user.domain.port.inbound

import io.github.snorrefo.event_driven_social.user.domain.model.User

interface CreateUserUseCase {
    fun createUser(username: String, displayName: String): User
}
