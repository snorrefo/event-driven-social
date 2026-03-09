package io.github.snorrefo.event_driven_social.timeline.adapter.outbound.post

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.GetPostsByAuthorsUseCase
import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import io.github.snorrefo.event_driven_social.timeline.domain.port.outbound.TimelinePostQueryPort
import org.springframework.stereotype.Component
import java.util.*

@Component
class TimelinePostQueryAdapter(
    private val getPostsByAuthorsUseCase: GetPostsByAuthorsUseCase
) : TimelinePostQueryPort {

    override fun findPostsByAuthors(authorIds: List<UUID>, page: Int, size: Int): List<TimelineEntry> =
        getPostsByAuthorsUseCase.getPostsByAuthors(authorIds, page, size)
            .map { it.toTimelineEntry() }
}

private fun Post.toTimelineEntry() = TimelineEntry(
    postId = id,
    authorId = authorId,
    content = content,
    createdAt = createdAt,
    inReplyToPostId = inReplyToPostId,
    mediaUrls = mediaUrls
)
