package io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.repository

import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {
    fun findByUsername(username: String): UserJpaEntity?
    fun existsByUsername(username: String): Boolean
}
