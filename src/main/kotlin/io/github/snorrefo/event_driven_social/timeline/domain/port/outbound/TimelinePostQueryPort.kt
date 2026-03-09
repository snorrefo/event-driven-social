package io.github.snorrefo.event_driven_social.timeline.domain.port.outbound

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import java.util.*

interface TimelinePostQueryPort {
    fun findPostsByAuthors(authorIds: List<UUID>, page: Int, size: Int): List<TimelineEntry>
}
