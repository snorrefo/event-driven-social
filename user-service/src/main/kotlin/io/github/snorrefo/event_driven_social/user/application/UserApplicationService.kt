package io.github.snorrefo.event_driven_social.user.application

import io.github.snorrefo.event_driven_social.user.domain.model.User
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.CreateUserUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.GetUserUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.outbound.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class UserApplicationService(
    private val userRepository: UserRepository,
) : CreateUserUseCase, GetUserUseCase {

    override fun createUser(username: String, displayName: String): User {
        require(!userRepository.existsByUsername(username)) { "Username '$username' is already taken" }
        val user = User(username = username, displayName = displayName)
        return userRepository.save(user)
    }

    @Transactional(readOnly = true)
    override fun getUser(id: UUID): User {
        return userRepository.findById(id) ?: throw NoSuchElementException("User not found: $id")
    }
}
