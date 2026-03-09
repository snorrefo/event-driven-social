package io.github.snorrefo.event_driven_social.identity.domain.port.inbound

import io.github.snorrefo.event_driven_social.identity.domain.model.User
import java.util.*

interface GetUserUseCase {
    fun getUser(userId: UUID): User?
    fun getUserByUsername(username: String): User?
}
