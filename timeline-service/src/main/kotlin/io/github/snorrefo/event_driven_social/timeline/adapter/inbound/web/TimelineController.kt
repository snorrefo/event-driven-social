package io.github.snorrefo.event_driven_social.timeline.adapter.inbound.web

import io.github.snorrefo.event_driven_social.timeline.adapter.inbound.web.dto.TimelineEntryResponse
import io.github.snorrefo.event_driven_social.timeline.domain.port.inbound.GetTimelineUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/timelines")
class TimelineController(
    private val getTimelineUseCase: GetTimelineUseCase,
) {

    @GetMapping("/{userId}")
    fun getTimeline(
        @PathVariable userId: UUID,
        @RequestParam(defaultValue = "0") offset: Long,
        @RequestParam(defaultValue = "20") limit: Long,
    ): List<TimelineEntryResponse> =
        getTimelineUseCase.getTimeline(userId, offset, limit).map { TimelineEntryResponse.from(it) }
}
