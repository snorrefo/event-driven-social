package io.github.snorrefo.event_driven_social.user.application

import io.github.snorrefo.event_driven_social.user.domain.model.User
import io.github.snorrefo.event_driven_social.user.domain.port.outbound.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.assertEquals

class UserApplicationServiceTest {

    private val userRepository: UserRepository = mock()
    private val service = UserApplicationService(userRepository)

    @Test
    fun `createUser saves and returns user`() {
        val user = User(username = "alice", displayName = "Alice")
        whenever(userRepository.existsByUsername("alice")).thenReturn(false)
        whenever(userRepository.save(any())).thenReturn(user)

        val result = service.createUser("alice", "Alice")

        assertEquals("alice", result.username)
        verify(userRepository).save(any())
    }

    @Test
    fun `createUser throws when username taken`() {
        whenever(userRepository.existsByUsername("alice")).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            service.createUser("alice", "Alice")
        }
    }

    @Test
    fun `getUser returns user when found`() {
        val id = UUID.randomUUID()
        val user = User(id = id, username = "alice", displayName = "Alice")
        whenever(userRepository.findById(id)).thenReturn(user)

        val result = service.getUser(id)

        assertEquals(id, result.id)
    }

    @Test
    fun `getUser throws when not found`() {
        val id = UUID.randomUUID()
        whenever(userRepository.findById(id)).thenReturn(null)

        assertThrows<NoSuchElementException> {
            service.getUser(id)
        }
    }
}
