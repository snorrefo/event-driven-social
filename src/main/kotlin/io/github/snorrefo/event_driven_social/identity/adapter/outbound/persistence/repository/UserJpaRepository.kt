package io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence.repository

import io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {

    fun findByUsername(username: String): UserJpaEntity?

    fun existsByUsername(username: String): Boolean
}
