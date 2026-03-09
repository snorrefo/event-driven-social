package io.github.snorrefo.event_driven_social.socialgraph.application

import io.github.snorrefo.event_driven_social.shared.event.UserFollowedData
import io.github.snorrefo.event_driven_social.shared.event.UserFollowedEvent
import io.github.snorrefo.event_driven_social.shared.event.UserUnfollowedData
import io.github.snorrefo.event_driven_social.shared.event.UserUnfollowedEvent
import io.github.snorrefo.event_driven_social.shared.model.OutboxEntry
import io.github.snorrefo.event_driven_social.shared.port.DomainEventPublisher
import io.github.snorrefo.event_driven_social.shared.port.EventSerializer
import io.github.snorrefo.event_driven_social.shared.port.OutboxRepository
import io.github.snorrefo.event_driven_social.socialgraph.domain.model.Follow
import io.github.snorrefo.event_driven_social.socialgraph.domain.port.inbound.FollowUserUseCase
import io.github.snorrefo.event_driven_social.socialgraph.domain.port.inbound.GetFollowedUsersUseCase
import io.github.snorrefo.event_driven_social.socialgraph.domain.port.outbound.FollowRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class SocialGraphApplicationService(
    private val followRepository: FollowRepository,
    private val outboxRepository: OutboxRepository,
    private val eventSerializer: EventSerializer,
    private val domainEventPublisher: DomainEventPublisher
) : FollowUserUseCase, GetFollowedUsersUseCase {

    @Transactional
    override fun follow(followerId: UUID, followedId: UUID): Follow {
        require(followerId != followedId) { "Users cannot follow themselves" }
        require(!followRepository.exists(followerId, followedId)) { "Already following this user" }

        val follow = Follow(followerId = followerId, followedId = followedId)
        val savedFollow = followRepository.save(follow)

        val event = UserFollowedEvent(
            data = UserFollowedData(
                followerId = followerId.toString(),
                followedUserId = followedId.toString(),
                followedAt = savedFollow.followedAt
            )
        )

        val outboxEntry = OutboxEntry(
            aggregateType = "follow",
            aggregateId = followerId,
            eventType = "user.followed",
            payload = eventSerializer.serialize(event)
        )
        outboxRepository.save(outboxEntry)

        domainEventPublisher.publish(event)

        return savedFollow
    }

    @Transactional
    override fun unfollow(followerId: UUID, followedId: UUID) {
        followRepository.delete(followerId, followedId)

        val event = UserUnfollowedEvent(
            data = UserUnfollowedData(
                followerId = followerId.toString(),
                unfollowedUserId = followedId.toString(),
                unfollowedAt = Instant.now()
            )
        )

        val outboxEntry = OutboxEntry(
            aggregateType = "follow",
            aggregateId = followerId,
            eventType = "user.unfollowed",
            payload = eventSerializer.serialize(event)
        )
        outboxRepository.save(outboxEntry)

        domainEventPublisher.publish(event)
    }

    override fun getFollowedUserIds(userId: UUID): List<UUID> =
        followRepository.findFollowedUserIds(userId)
}
