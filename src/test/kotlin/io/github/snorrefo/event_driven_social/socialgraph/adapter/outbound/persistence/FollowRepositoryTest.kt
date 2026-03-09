package io.github.snorrefo.event_driven_social.socialgraph.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.shared.TestContainersConfiguration
import io.github.snorrefo.event_driven_social.socialgraph.adapter.outbound.persistence.repository.FollowJpaRepository
import io.github.snorrefo.event_driven_social.socialgraph.domain.model.Follow
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
@ActiveProfiles("test")
class FollowRepositoryTest {

    @Autowired
    private lateinit var followJpaRepository: FollowJpaRepository

    private val followRepository by lazy { FollowRepositoryAdapter(followJpaRepository) }

    @Test
    fun `should save and check follow exists`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()

        followRepository.save(Follow(followerId = followerId, followedId = followedId))

        assertTrue(followRepository.exists(followerId, followedId))
        assertFalse(followRepository.exists(followedId, followerId))
    }

    @Test
    fun `should find followed user ids`() {
        val followerId = UUID.randomUUID()
        val followed1 = UUID.randomUUID()
        val followed2 = UUID.randomUUID()
        val followed3 = UUID.randomUUID()

        followRepository.save(Follow(followerId = followerId, followedId = followed1))
        followRepository.save(Follow(followerId = followerId, followedId = followed2))
        followRepository.save(Follow(followerId = followerId, followedId = followed3))

        val followedIds = followRepository.findFollowedUserIds(followerId)

        assertEquals(3, followedIds.size)
        assertTrue(followedIds.containsAll(listOf(followed1, followed2, followed3)))
    }

    @Test
    fun `should return empty list when not following anyone`() {
        val userId = UUID.randomUUID()

        val followedIds = followRepository.findFollowedUserIds(userId)

        assertTrue(followedIds.isEmpty())
    }

    @Test
    fun `should delete follow relationship`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()

        followRepository.save(Follow(followerId = followerId, followedId = followedId))
        assertTrue(followRepository.exists(followerId, followedId))

        followRepository.delete(followerId, followedId)
        assertFalse(followRepository.exists(followerId, followedId))
    }

    @Test
    fun `should not return follows from other users`() {
        val user1 = UUID.randomUUID()
        val user2 = UUID.randomUUID()
        val target = UUID.randomUUID()

        followRepository.save(Follow(followerId = user1, followedId = target))
        followRepository.save(Follow(followerId = user2, followedId = target))

        val user1Follows = followRepository.findFollowedUserIds(user1)
        val user2Follows = followRepository.findFollowedUserIds(user2)

        assertEquals(1, user1Follows.size)
        assertEquals(target, user1Follows[0])
        assertEquals(1, user2Follows.size)
        assertEquals(target, user2Follows[0])
    }
}
