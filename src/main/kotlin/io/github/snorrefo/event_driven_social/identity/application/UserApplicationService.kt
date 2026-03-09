package io.github.snorrefo.event_driven_social.identity.application

import io.github.snorrefo.event_driven_social.identity.domain.model.User
import io.github.snorrefo.event_driven_social.identity.domain.port.inbound.CreateUserUseCase
import io.github.snorrefo.event_driven_social.identity.domain.port.inbound.GetUserUseCase
import io.github.snorrefo.event_driven_social.identity.domain.port.outbound.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserApplicationService(
    private val userRepository: UserRepository
) : CreateUserUseCase, GetUserUseCase {

    @Transactional
    override fun createUser(
        username: String,
        displayName: String,
        bio: String?,
        avatarUrl: String?
    ): User {
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(username.length in 3..50) { "Username must be 3-50 characters" }
        require(!userRepository.existsByUsername(username)) { "Username already exists" }

        val user = User(
            username = username,
            displayName = displayName,
            bio = bio,
            avatarUrl = avatarUrl
        )

        return userRepository.save(user)
    }

    override fun getUser(userId: UUID): User? =
        userRepository.findById(userId)

    override fun getUserByUsername(username: String): User? =
        userRepository.findByUsername(username)
}
