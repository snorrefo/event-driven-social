package io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.identity.adapter.outbound.persistence.repository.UserJpaRepository
import io.github.snorrefo.event_driven_social.identity.domain.model.User
import io.github.snorrefo.event_driven_social.shared.TestContainersConfiguration
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import kotlin.test.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    private lateinit var entityManager: EntityManager

    private val userRepository by lazy { UserRepositoryAdapter(userJpaRepository) }

    @Test
    fun `should save and retrieve user`() {
        val user = User(username = "johndoe", displayName = "John Doe")

        val saved = userRepository.save(user)
        val found = userRepository.findById(saved.id)

        assertNotNull(found)
        assertEquals(user.username, found.username)
        assertEquals(user.displayName, found.displayName)
    }

    @Test
    fun `should save user with bio and avatar`() {
        val user = User(
            username = "janedoe",
            displayName = "Jane Doe",
            bio = "Software engineer",
            avatarUrl = "https://example.com/avatar.jpg"
        )

        val saved = userRepository.save(user)
        val found = userRepository.findById(saved.id)

        assertNotNull(found)
        assertEquals(user.bio, found.bio)
        assertEquals(user.avatarUrl, found.avatarUrl)
    }

    @Test
    fun `should find user by username`() {
        val user = User(username = "johndoe", displayName = "John Doe")
        userRepository.save(user)

        val found = userRepository.findByUsername("johndoe")

        assertNotNull(found)
        assertEquals("johndoe", found.username)
    }

    @Test
    fun `should return null when username not found`() {
        val found = userRepository.findByUsername("nonexistent")

        assertNull(found)
    }

    @Test
    fun `should check if username exists`() {
        val user = User(username = "johndoe", displayName = "John Doe")
        userRepository.save(user)

        assertTrue(userRepository.existsByUsername("johndoe"))
        assertFalse(userRepository.existsByUsername("janedoe"))
    }

    @Test
    fun `should increment and read followers count`() {
        val user = User(username = "counttest1", displayName = "Count Test")
        val saved = userRepository.save(user)

        userRepository.incrementFollowersCount(saved.id)
        userJpaRepository.flush()
        entityManager.clear()

        val found = userRepository.findById(saved.id)!!
        assertEquals(1, found.followersCount)
    }

    @Test
    fun `should decrement followers count`() {
        val user = User(username = "counttest2", displayName = "Count Test", followersCount = 5)
        val saved = userRepository.save(user)

        userRepository.decrementFollowersCount(saved.id)
        userJpaRepository.flush()
        entityManager.clear()

        val found = userRepository.findById(saved.id)!!
        assertEquals(4, found.followersCount)
    }

    @Test
    fun `should not decrement followers count below zero`() {
        val user = User(username = "counttest3", displayName = "Count Test", followersCount = 0)
        val saved = userRepository.save(user)

        userRepository.decrementFollowersCount(saved.id)
        userJpaRepository.flush()
        entityManager.clear()

        val found = userRepository.findById(saved.id)!!
        assertEquals(0, found.followersCount)
    }

    @Test
    fun `should increment following count`() {
        val user = User(username = "counttest4", displayName = "Count Test")
        val saved = userRepository.save(user)

        userRepository.incrementFollowingCount(saved.id)
        userJpaRepository.flush()
        entityManager.clear()

        val found = userRepository.findById(saved.id)!!
        assertEquals(1, found.followingCount)
    }

    @Test
    fun `should increment posts count`() {
        val user = User(username = "counttest5", displayName = "Count Test")
        val saved = userRepository.save(user)

        userRepository.incrementPostsCount(saved.id)
        userRepository.incrementPostsCount(saved.id)
        userJpaRepository.flush()
        entityManager.clear()

        val found = userRepository.findById(saved.id)!!
        assertEquals(2, found.postsCount)
    }

    @Test
    fun `should enforce unique username constraint`() {
        val user1 = User(username = "johndoe", displayName = "John Doe")
        userRepository.save(user1)

        val user2 = User(username = "johndoe", displayName = "John Doe 2")

        assertFailsWith<DataIntegrityViolationException> {
            userRepository.save(user2)
            userJpaRepository.flush()
        }
    }
}
