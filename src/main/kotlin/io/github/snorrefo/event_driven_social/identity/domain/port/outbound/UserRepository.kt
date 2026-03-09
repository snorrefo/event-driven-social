package io.github.snorrefo.event_driven_social.identity.domain.port.outbound

import io.github.snorrefo.event_driven_social.identity.domain.model.User
import java.util.*

interface UserRepository {
    fun save(user: User): User
    fun findById(userId: UUID): User?
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}
