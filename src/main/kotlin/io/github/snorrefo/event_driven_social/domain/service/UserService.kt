package io.github.snorrefo.event_driven_social.domain.service


import io.github.snorrefo.event_driven_social.domain.model.User
import io.github.snorrefo.event_driven_social.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun createUser(
        username: String,
        displayName: String,
        bio: String? = null,
        avatarUrl: String? = null
    ): User {
        // Validate
        require(username.isNotBlank()) { "Username cannot be blank" }
        require(username.length in 3..50) { "Username must be 3-50 characters" }
        require(!userRepository.existsByUsername(username)) {
            "Username already exists"
        }

        // Create user
        val user = User(
            username = username,
            displayName = displayName,
            bio = bio,
            avatarUrl = avatarUrl
        )

        return userRepository.save(user)
    }

    fun getUser(userId: UUID): User? {
        return userRepository.findById(userId).orElse(null)
    }

    fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
}