package io.github.snorrefo.event_driven_social.identity.domain.port.inbound

import io.github.snorrefo.event_driven_social.identity.domain.model.User

interface CreateUserUseCase {
    fun createUser(
        username: String,
        displayName: String,
        bio: String? = null,
        avatarUrl: String? = null
    ): User
}
