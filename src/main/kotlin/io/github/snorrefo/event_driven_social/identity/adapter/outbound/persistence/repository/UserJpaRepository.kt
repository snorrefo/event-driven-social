package io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence.repository

import io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {

    fun findByUsername(username: String): UserJpaEntity?

    fun existsByUsername(username: String): Boolean

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.followersCount = u.followersCount + 1 WHERE u.id = :userId")
    fun incrementFollowersCount(@Param("userId") userId: UUID)

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.followersCount = u.followersCount - 1 WHERE u.id = :userId AND u.followersCount > 0")
    fun decrementFollowersCount(@Param("userId") userId: UUID)

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.followingCount = u.followingCount + 1 WHERE u.id = :userId")
    fun incrementFollowingCount(@Param("userId") userId: UUID)

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.followingCount = u.followingCount - 1 WHERE u.id = :userId AND u.followingCount > 0")
    fun decrementFollowingCount(@Param("userId") userId: UUID)

    @Modifying
    @Query("UPDATE UserJpaEntity u SET u.postsCount = u.postsCount + 1 WHERE u.id = :userId")
    fun incrementPostsCount(@Param("userId") userId: UUID)
}
