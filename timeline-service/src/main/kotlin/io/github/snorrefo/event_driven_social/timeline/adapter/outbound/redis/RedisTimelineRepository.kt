package io.github.snorrefo.event_driven_social.timeline.adapter.outbound.redis

import tools.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.TimelineRepository
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class RedisTimelineRepository(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper,
) : TimelineRepository {

    override fun addEntry(userId: UUID, entry: TimelineEntry) {
        val key = "timeline:$userId"
        val value = objectMapper.writeValueAsString(entry)
        redisTemplate.opsForZSet().add(key, value, entry.createdAt.toEpochMilli().toDouble())
    }

    override fun getEntries(userId: UUID, offset: Long, limit: Long): List<TimelineEntry> {
        val key = "timeline:$userId"
        val values = redisTemplate.opsForZSet().reverseRange(key, offset, offset + limit - 1) ?: emptySet()
        return values.map { objectMapper.readValue(it, TimelineEntry::class.java) }
    }

    override fun removeEntriesByAuthor(userId: UUID, authorId: UUID) {
        val key = "timeline:$userId"
        val all = redisTemplate.opsForZSet().range(key, 0, -1) ?: return
        val toRemove = all.filter {
            val entry = objectMapper.readValue(it, TimelineEntry::class.java)
            entry.authorId == authorId
        }
        if (toRemove.isNotEmpty()) {
            redisTemplate.opsForZSet().remove(key, *toRemove.toTypedArray())
        }
    }
}
