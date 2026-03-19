package io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.entity.UserJpaEntity
import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.repository.UserJpaRepository
import io.github.snorrefo.event_driven_social.user.domain.model.User
import io.github.snorrefo.event_driven_social.user.domain.port.outbound.UserRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class UserRepositoryAdapter(
    private val jpaRepository: UserJpaRepository,
) : UserRepository {

    override fun save(user: User): User =
        jpaRepository.save(UserJpaEntity.fromDomain(user)).toDomain()

    override fun findById(id: UUID): User? =
        jpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findByUsername(username: String): User? =
        jpaRepository.findByUsername(username)?.toDomain()

    override fun existsByUsername(username: String): Boolean =
        jpaRepository.existsByUsername(username)
}
