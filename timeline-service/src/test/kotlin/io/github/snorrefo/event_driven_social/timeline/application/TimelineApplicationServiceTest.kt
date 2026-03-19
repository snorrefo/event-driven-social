package io.github.snorrefo.event_driven_social.timeline.application

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.FollowerProjectionRepository
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.TimelineRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.UUID

class TimelineApplicationServiceTest {

    private val timelineRepository: TimelineRepository = mock()
    private val followerProjectionRepository: FollowerProjectionRepository = mock()
    private val service = TimelineApplicationService(timelineRepository, followerProjectionRepository)

    @Test
    fun `fanOutPost writes to each follower timeline`() {
        val authorId = UUID.randomUUID()
        val follower1 = UUID.randomUUID()
        val follower2 = UUID.randomUUID()
        whenever(followerProjectionRepository.getFollowers(authorId)).thenReturn(setOf(follower1, follower2))

        val entry = TimelineEntry(
            postId = UUID.randomUUID(), authorId = authorId, content = "Hello", createdAt = Instant.now(),
        )
        service.fanOutPost(entry)

        verify(timelineRepository, times(2)).addEntry(any(), eq(entry))
    }

    @Test
    fun `handleFollow adds follower to projection`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()

        service.handleFollow(followerId, followedId)

        verify(followerProjectionRepository).addFollower(followedId, followerId)
    }

    @Test
    fun `handleUnfollow removes follower and cleans timeline`() {
        val followerId = UUID.randomUUID()
        val followedId = UUID.randomUUID()

        service.handleUnfollow(followerId, followedId)

        verify(followerProjectionRepository).removeFollower(followedId, followerId)
        verify(timelineRepository).removeEntriesByAuthor(followerId, followedId)
    }

    @Test
    fun `getTimeline delegates to repository`() {
        val userId = UUID.randomUUID()
        service.getTimeline(userId, 0, 20)

        verify(timelineRepository).getEntries(userId, 0, 20)
    }
}
