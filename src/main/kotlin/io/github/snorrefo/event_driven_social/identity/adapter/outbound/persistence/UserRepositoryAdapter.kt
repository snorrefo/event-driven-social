package io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence.entity.UserJpaEntity
import io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence.repository.UserJpaRepository
import io.github.snorrefo.event_driven_social.identity.domain.model.User
import io.github.snorrefo.event_driven_social.identity.domain.port.outbound.UserRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserRepositoryAdapter(
    private val jpaRepository: UserJpaRepository
) : UserRepository {

    override fun save(user: User): User =
        jpaRepository.save(user.toJpaEntity()).toDomain()

    override fun findById(userId: UUID): User? =
        jpaRepository.findById(userId).orElse(null)?.toDomain()

    override fun findByUsername(username: String): User? =
        jpaRepository.findByUsername(username)?.toDomain()

    override fun existsByUsername(username: String): Boolean =
        jpaRepository.existsByUsername(username)
}

private fun User.toJpaEntity() = UserJpaEntity(
    id = id,
    username = username,
    displayName = displayName,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)

private fun UserJpaEntity.toDomain() = User(
    id = id,
    username = username,
    displayName = displayName,
    bio = bio,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)
