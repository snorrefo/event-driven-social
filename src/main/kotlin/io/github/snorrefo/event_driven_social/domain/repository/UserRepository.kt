package io.github.snorrefo.event_driven_social.domain.repository


import io.github.snorrefo.event_driven_social.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    fun findByUsername(username: String): User?

    fun existsByUsername(username: String): Boolean
}