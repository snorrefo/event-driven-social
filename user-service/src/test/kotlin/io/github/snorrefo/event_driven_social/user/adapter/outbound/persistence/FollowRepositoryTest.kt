package io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.user.TestContainersConfiguration
import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.entity.FollowJpaEntity
import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.repository.FollowJpaRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
class FollowRepositoryTest {

    @Autowired
    lateinit var followJpaRepository: FollowJpaRepository

    @Test
    fun `save and find follow relationship`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()
        followJpaRepository.save(FollowJpaEntity(followerId = followerId, followedId = followedId))

        assertTrue(followJpaRepository.existsByFollowerIdAndFollowedId(followerId, followedId))
    }

    @Test
    fun `find followers returns correct IDs`() {
        val userId = UUID.randomUUID()
        val follower1 = UUID.randomUUID()
        val follower2 = UUID.randomUUID()
        followJpaRepository.save(FollowJpaEntity(followerId = follower1, followedId = userId))
        followJpaRepository.save(FollowJpaEntity(followerId = follower2, followedId = userId))

        val followers = followJpaRepository.findByFollowedId(userId)

        assertEquals(2, followers.size)
    }
}
