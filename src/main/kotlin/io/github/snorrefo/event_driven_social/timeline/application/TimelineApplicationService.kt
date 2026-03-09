package io.github.snorrefo.event_driven_social.timeline.application

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import io.github.snorrefo.event_driven_social.timeline.domain.port.inbound.GetTimelineUseCase
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.FollowedUsersQueryPort
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.TimelinePostQueryPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class TimelineApplicationService(
    private val followedUsersQueryPort: FollowedUsersQueryPort,
    private val timelinePostQueryPort: TimelinePostQueryPort
) : GetTimelineUseCase {

    override fun getTimeline(userId: UUID, page: Int, size: Int): List<TimelineEntry> {
        val followedUserIds = followedUsersQueryPort.getFollowedUserIds(userId)
        val authorIds = followedUserIds + userId
        return timelinePostQueryPort.findPostsByAuthors(authorIds, page, size)
    }
}
