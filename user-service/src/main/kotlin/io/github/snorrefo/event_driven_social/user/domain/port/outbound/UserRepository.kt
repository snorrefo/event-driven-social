package io.github.snorrefo.event_driven_social.user.domain.port.outbound

import io.github.snorrefo.event_driven_social.user.domain.model.User
import java.util.UUID

interface UserRepository {
    fun save(user: User): User
    fun findById(id: UUID): User?
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}
