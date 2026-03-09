package io.github.snorrefo.event_driven_social.timeline.application

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.FollowedUsersQueryPort
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.TimelinePostQueryPort
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimelineApplicationServiceTest {

    private val followedUsersQueryPort = mock<FollowedUsersQueryPort>()
    private val timelinePostQueryPort = mock<TimelinePostQueryPort>()

    private val service = TimelineApplicationService(followedUsersQueryPort, timelinePostQueryPort)

    @Test
    fun `should return posts from followed users and own posts`() {
        val userId = UUID.randomUUID()
        val followed1 = UUID.randomUUID()
        val followed2 = UUID.randomUUID()

        val entries = listOf(
            TimelineEntry(postId = UUID.randomUUID(), authorId = followed1, content = "Post from followed 1", createdAt = Instant.now()),
            TimelineEntry(postId = UUID.randomUUID(), authorId = followed2, content = "Post from followed 2", createdAt = Instant.now()),
            TimelineEntry(postId = UUID.randomUUID(), authorId = userId, content = "Own post", createdAt = Instant.now())
        )

        whenever(followedUsersQueryPort.getFollowedUserIds(userId)).thenReturn(listOf(followed1, followed2))
        whenever(timelinePostQueryPort.findPostsByAuthors(eq(listOf(followed1, followed2, userId)), eq(0), eq(20)))
            .thenReturn(entries)

        val result = service.getTimeline(userId)

        assertEquals(3, result.size)
        verify(timelinePostQueryPort).findPostsByAuthors(listOf(followed1, followed2, userId), 0, 20)
    }

    @Test
    fun `should include own user id in author list`() {
        val userId = UUID.randomUUID()
        val captor = argumentCaptor<List<UUID>>()

        whenever(followedUsersQueryPort.getFollowedUserIds(userId)).thenReturn(emptyList())
        whenever(timelinePostQueryPort.findPostsByAuthors(captor.capture(), eq(0), eq(20)))
            .thenReturn(emptyList())

        service.getTimeline(userId)

        val authorIds = captor.firstValue
        assertTrue(authorIds.contains(userId))
        assertEquals(1, authorIds.size)
    }

    @Test
    fun `should return only own posts when not following anyone`() {
        val userId = UUID.randomUUID()
        val ownPost = TimelineEntry(
            postId = UUID.randomUUID(), authorId = userId, content = "My post", createdAt = Instant.now()
        )

        whenever(followedUsersQueryPort.getFollowedUserIds(userId)).thenReturn(emptyList())
        whenever(timelinePostQueryPort.findPostsByAuthors(eq(listOf(userId)), eq(0), eq(20)))
            .thenReturn(listOf(ownPost))

        val result = service.getTimeline(userId)

        assertEquals(1, result.size)
        assertEquals(userId, result[0].authorId)
    }

    @Test
    fun `should forward pagination parameters`() {
        val userId = UUID.randomUUID()

        whenever(followedUsersQueryPort.getFollowedUserIds(userId)).thenReturn(emptyList())
        whenever(timelinePostQueryPort.findPostsByAuthors(any(), eq(2), eq(10)))
            .thenReturn(emptyList())

        service.getTimeline(userId, page = 2, size = 10)

        verify(timelinePostQueryPort).findPostsByAuthors(any(), eq(2), eq(10))
    }

    @Test
    fun `should return empty list when no posts exist`() {
        val userId = UUID.randomUUID()
        val followed = UUID.randomUUID()

        whenever(followedUsersQueryPort.getFollowedUserIds(userId)).thenReturn(listOf(followed))
        whenever(timelinePostQueryPort.findPostsByAuthors(any(), any(), any()))
            .thenReturn(emptyList())

        val result = service.getTimeline(userId)

        assertTrue(result.isEmpty())
    }
}
