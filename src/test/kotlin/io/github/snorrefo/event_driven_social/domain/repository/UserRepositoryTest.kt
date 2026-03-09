package io.github.snorrefo.event_driven_social.domain.repository

import io.github.snorrefo.event_driven_social.domain.TestContainersConfiguration
import io.github.snorrefo.event_driven_social.domain.model.User
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
    private lateinit var userRepository: UserRepository

    @Test
    fun `should save and retrieve user`() {
        // Given
        val user = User(
            username = "johndoe",
            displayName = "John Doe"
        )

        // When
        val saved = userRepository.save(user)
        val found = userRepository.findById(saved.id)

        // Then
        assertTrue(found.isPresent)
        assertEquals(user.username, found.get().username)
        assertEquals(user.displayName, found.get().displayName)
    }

    @Test
    fun `should save user with bio and avatar`() {
        // Given
        val user = User(
            username = "janedoe",
            displayName = "Jane Doe",
            bio = "Software engineer",
            avatarUrl = "https://example.com/avatar.jpg"
        )

        // When
        val saved = userRepository.save(user)
        val found = userRepository.findById(saved.id)

        // Then
        assertTrue(found.isPresent)
        assertEquals(user.bio, found.get().bio)
        assertEquals(user.avatarUrl, found.get().avatarUrl)
    }

    @Test
    fun `should find user by username`() {
        // Given
        val user = User(
            username = "johndoe",
            displayName = "John Doe"
        )
        userRepository.save(user)

        // When
        val found = userRepository.findByUsername("johndoe")

        // Then
        assertNotNull(found)
        assertEquals("johndoe", found.username)
    }

    @Test
    fun `should return null when username not found`() {
        // When
        val found = userRepository.findByUsername("nonexistent")

        // Then
        assertNull(found)
    }

    @Test
    fun `should check if username exists`() {
        // Given
        val user = User(
            username = "johndoe",
            displayName = "John Doe"
        )
        userRepository.save(user)

        // When/Then
        assertTrue(userRepository.existsByUsername("johndoe"))
        assertFalse(userRepository.existsByUsername("janedoe"))
    }

    @Test
    fun `should enforce unique username constraint`() {
        // Given
        val user1 = User(username = "johndoe", displayName = "John Doe")
        userRepository.save(user1)

        val user2 = User(username = "johndoe", displayName = "John Doe 2")

        // When/Then
        assertFailsWith<DataIntegrityViolationException> {
            userRepository.save(user2)
            userRepository.flush() // Force constraint check
        }
    }
}