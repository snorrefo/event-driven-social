package io.github.snorrefo.event_driven_social.timeline.adapter.inbound.web

import io.github.snorrefo.event_driven_social.timeline.adapter.inbound.web.dto.TimelineEntryResponse
import io.github.snorrefo.event_driven_social.timeline.adapter.inbound.web.dto.toResponse
import io.github.snorrefo.event_driven_social.timeline.domain.port.inbound.GetTimelineUseCase
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/timeline")
class TimelineController(
    private val getTimelineUseCase: GetTimelineUseCase
) {

    @GetMapping
    fun getTimeline(
        @RequestHeader("X-User-Id") userId: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<TimelineEntryResponse> {
        return getTimelineUseCase.getTimeline(UUID.fromString(userId), page, size)
            .map { it.toResponse() }
    }
}
