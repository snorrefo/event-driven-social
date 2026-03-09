package io.github.snorrefo.event_driven_social.socialgraph.domain.model

import java.time.Instant
import java.util.*

data class Follow(
    val followerId: UUID,
    val followedId: UUID,
    val followedAt: Instant = Instant.now()
)
