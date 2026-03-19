package io.github.snorrefo.event_driven_social.timeline.domain.port.inbound

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import java.util.UUID

interface GetTimelineUseCase {
    fun getTimeline(userId: UUID, offset: Long = 0, limit: Long = 20): List<TimelineEntry>
}
