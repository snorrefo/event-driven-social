package io.github.snorrefo.event_driven_social.user.domain.port.inbound

import io.github.snorrefo.event_driven_social.user.domain.model.User
import java.util.UUID

interface GetUserUseCase {
    fun getUser(id: UUID): User
}
