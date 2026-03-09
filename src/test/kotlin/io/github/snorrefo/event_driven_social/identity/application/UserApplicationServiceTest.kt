package io.github.snorrefo.event_driven_social.identity.application

import io.github.snorrefo.event_driven_social.identity.domain.model.User
import io.github.snorrefo.event_driven_social.identity.domain.port.outbound.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserApplicationServiceTest {

    private val userRepository = mock<UserRepository>()
    private val userService = UserApplicationService(userRepository)

    @Test
    fun `should create user successfully`() {
        val username = "johndoe"
        val displayName = "John Doe"

        whenever(userRepository.existsByUsername(username)).thenReturn(false)
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = userService.createUser(username, displayName)

        assertNotNull(result)
        assertEquals(username, result.username)
        assertEquals(displayName, result.displayName)
        verify(userRepository).existsByUsername(username)
        verify(userRepository).save(any())
    }

    @Test
    fun `should create user with bio and avatar`() {
        val username = "janedoe"
        val displayName = "Jane Doe"
        val bio = "Software engineer"
        val avatarUrl = "https://example.com/avatar.jpg"

        whenever(userRepository.existsByUsername(username)).thenReturn(false)
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] }

        val result = userService.createUser(
            username = username,
            displayName = displayName,
            bio = bio,
            avatarUrl = avatarUrl
        )

        assertEquals(bio, result.bio)
        assertEquals(avatarUrl, result.avatarUrl)
    }

    @Test
    fun `should reject blank username`() {
        val exception = assertThrows<IllegalArgumentException> {
            userService.createUser("   ", "John Doe")
        }

        assertEquals("Username cannot be blank", exception.message)
        verify(userRepository, never()).save(any())
    }

    @Test
    fun `should reject username shorter than 3 characters`() {
        whenever(userRepository.existsByUsername("ab")).thenReturn(false)

        val exception = assertThrows<IllegalArgumentException> {
            userService.createUser("ab", "John Doe")
        }

        assertEquals("Username must be 3-50 characters", exception.message)
    }

    @Test
    fun `should reject username longer than 50 characters`() {
        val longUsername = "a".repeat(51)
        whenever(userRepository.existsByUsername(longUsername)).thenReturn(false)

        val exception = assertThrows<IllegalArgumentException> {
            userService.createUser(longUsername, "John Doe")
        }

        assertEquals("Username must be 3-50 characters", exception.message)
    }

    @Test
    fun `should reject duplicate username`() {
        val username = "johndoe"

        whenever(userRepository.existsByUsername(username)).thenReturn(true)

        val exception = assertThrows<IllegalArgumentException> {
            userService.createUser(username, "John Doe")
        }

        assertEquals("Username already exists", exception.message)
        verify(userRepository, never()).save(any())
    }

    @Test
    fun `should get user by id when exists`() {
        val userId = UUID.randomUUID()
        val user = User(id = userId, username = "johndoe", displayName = "John Doe")

        whenever(userRepository.findById(userId)).thenReturn(user)

        val result = userService.getUser(userId)

        assertNotNull(result)
        assertEquals(userId, result.id)
    }

    @Test
    fun `should return null when user does not exist`() {
        val userId = UUID.randomUUID()

        whenever(userRepository.findById(userId)).thenReturn(null)

        val result = userService.getUser(userId)

        assertNull(result)
    }

    @Test
    fun `should get user by username`() {
        val username = "johndoe"
        val user = User(id = UUID.randomUUID(), username = username, displayName = "John Doe")

        whenever(userRepository.findByUsername(username)).thenReturn(user)

        val result = userService.getUserByUsername(username)

        assertNotNull(result)
        assertEquals(username, result!!.username)
    }
}
