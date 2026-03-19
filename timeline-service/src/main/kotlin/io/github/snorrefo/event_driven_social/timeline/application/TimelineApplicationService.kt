package io.github.snorrefo.event_driven_social.timeline.application

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import io.github.snorrefo.event_driven_social.timeline.domain.port.inbound.GetTimelineUseCase
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.FollowerProjectionRepository
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.TimelineRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TimelineApplicationService(
    private val timelineRepository: TimelineRepository,
    private val followerProjectionRepository: FollowerProjectionRepository,
) : GetTimelineUseCase {

    override fun getTimeline(userId: UUID, offset: Long, limit: Long): List<TimelineEntry> =
        timelineRepository.getEntries(userId, offset, limit)

    fun fanOutPost(entry: TimelineEntry) {
        val followers = followerProjectionRepository.getFollowers(entry.authorId)
        followers.forEach { followerId ->
            timelineRepository.addEntry(followerId, entry)
        }
    }

    fun handleFollow(followerId: UUID, followedId: UUID) {
        followerProjectionRepository.addFollower(followedId, followerId)
    }

    fun handleUnfollow(followerId: UUID, followedId: UUID) {
        followerProjectionRepository.removeFollower(followedId, followerId)
        timelineRepository.removeEntriesByAuthor(followerId, followedId)
    }
}
