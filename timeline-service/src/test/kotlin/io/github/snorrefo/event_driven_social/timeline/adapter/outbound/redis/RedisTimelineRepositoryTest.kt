package io.github.snorrefo.event_driven_social.timeline.adapter.outbound.redis

import tools.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.timeline.TestContainersConfiguration
import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.bean.override.mockito.MockitoBean
import software.amazon.awssdk.services.sqs.SqsClient
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(properties = ["app.sqs.timeline-events-queue-url=http://localhost:4566/queue/test"])
@Import(TestContainersConfiguration::class)
class RedisTimelineRepositoryTest {

    @MockitoBean
    lateinit var sqsClient: SqsClient

    @Autowired
    lateinit var redisTemplate: StringRedisTemplate

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var repository: RedisTimelineRepository

    @BeforeEach
    fun setUp() {
        repository = RedisTimelineRepository(redisTemplate, objectMapper)
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
    }

    @Test
    fun `add and retrieve timeline entries`() {
        val userId = UUID.randomUUID()
        val entry1 = TimelineEntry(UUID.randomUUID(), UUID.randomUUID(), "First", Instant.now().minusSeconds(10))
        val entry2 = TimelineEntry(UUID.randomUUID(), UUID.randomUUID(), "Second", Instant.now())

        repository.addEntry(userId, entry1)
        repository.addEntry(userId, entry2)

        val entries = repository.getEntries(userId, 0, 10)

        assertEquals(2, entries.size)
        assertEquals("Second", entries[0].content)
    }

    @Test
    fun `remove entries by author`() {
        val userId = UUID.randomUUID()
        val authorId = UUID.randomUUID()
        val entry1 = TimelineEntry(UUID.randomUUID(), authorId, "Remove me", Instant.now())
        val entry2 = TimelineEntry(UUID.randomUUID(), UUID.randomUUID(), "Keep me", Instant.now().plusSeconds(1))

        repository.addEntry(userId, entry1)
        repository.addEntry(userId, entry2)
        repository.removeEntriesByAuthor(userId, authorId)

        val entries = repository.getEntries(userId, 0, 10)
        assertEquals(1, entries.size)
        assertEquals("Keep me", entries[0].content)
    }
}
