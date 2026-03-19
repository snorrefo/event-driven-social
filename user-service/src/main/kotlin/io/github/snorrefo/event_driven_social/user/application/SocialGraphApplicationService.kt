package io.github.snorrefo.event_driven_social.user.application

import io.github.snorrefo.event_driven_social.shared.event.UserFollowedEvent
import io.github.snorrefo.event_driven_social.shared.event.UserUnfollowedEvent
import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import io.github.snorrefo.event_driven_social.shared.port.EventSerializer
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import io.github.snorrefo.event_driven_social.user.domain.model.Follow
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.FollowUserUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.GetFollowedUsersUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.GetFollowersUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.UnfollowUserUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.outbound.FollowRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class SocialGraphApplicationService(
    private val followRepository: FollowRepository,
    private val outboxRepository: OutboxRepository,
    private val eventSerializer: EventSerializer,
) : FollowUserUseCase, UnfollowUserUseCase, GetFollowersUseCase, GetFollowedUsersUseCase {

    override fun follow(followerId: UUID, followedId: UUID) {
        require(followerId != followedId) { "Cannot follow yourself" }
        require(!followRepository.exists(followerId, followedId)) { "Already following this user" }

        followRepository.save(Follow(followerId = followerId, followedId = followedId))

        val event = UserFollowedEvent(followerId = followerId, followedId = followedId)
        outboxRepository.save(
            OutboxEntry(eventType = event.eventType, payload = eventSerializer.serialize(event))
        )
    }

    override fun unfollow(followerId: UUID, followedId: UUID) {
        require(followRepository.exists(followerId, followedId)) { "Not following this user" }

        followRepository.delete(followerId, followedId)

        val event = UserUnfollowedEvent(followerId = followerId, followedId = followedId)
        outboxRepository.save(
            OutboxEntry(eventType = event.eventType, payload = eventSerializer.serialize(event))
        )
    }

    @Transactional(readOnly = true)
    override fun getFollowers(userId: UUID): List<UUID> = followRepository.findFollowers(userId)

    @Transactional(readOnly = true)
    override fun getFollowedUsers(userId: UUID): List<UUID> = followRepository.findFollowedUsers(userId)
}
