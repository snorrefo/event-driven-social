package io.github.snorrefo.event_driven_social.timeline.domain.port.inbound

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import java.util.*

interface GetTimelineUseCase {
    fun getTimeline(userId: UUID, page: Int = 0, size: Int = 20): List<TimelineEntry>
}
