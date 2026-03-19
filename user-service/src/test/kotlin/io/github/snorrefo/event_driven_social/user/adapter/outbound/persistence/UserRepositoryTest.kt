package io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence

import io.github.snorrefo.event_driven_social.user.TestContainersConfiguration
import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.entity.UserJpaEntity
import io.github.snorrefo.event_driven_social.user.adapter.outbound.persistence.repository.UserJpaRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfiguration::class)
class UserRepositoryTest {

    @Autowired
    lateinit var userJpaRepository: UserJpaRepository

    @Test
    fun `save and find user by username`() {
        val entity = UserJpaEntity(username = "alice", displayName = "Alice")
        userJpaRepository.save(entity)

        val found = userJpaRepository.findByUsername("alice")

        assertNotNull(found)
        assertEquals("alice", found.username)
    }

    @Test
    fun `existsByUsername returns true for existing user`() {
        val entity = UserJpaEntity(username = "bob", displayName = "Bob")
        userJpaRepository.save(entity)

        assertTrue(userJpaRepository.existsByUsername("bob"))
    }
}
