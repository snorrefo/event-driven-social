package io.github.snorrefo.event_driven_social.user.adapter.inbound.web

import io.github.snorrefo.event_driven_social.user.adapter.inbound.web.dto.FollowRequest
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.FollowUserUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.GetFollowedUsersUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.GetFollowersUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.UnfollowUserUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users/{userId}")
class FollowController(
    private val followUserUseCase: FollowUserUseCase,
    private val unfollowUserUseCase: UnfollowUserUseCase,
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowedUsersUseCase: GetFollowedUsersUseCase,
) {

    @PostMapping("/following")
    @ResponseStatus(HttpStatus.CREATED)
    fun follow(@PathVariable userId: UUID, @RequestBody request: FollowRequest) {
        followUserUseCase.follow(userId, request.followedId)
    }

    @DeleteMapping("/following/{followedId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun unfollow(@PathVariable userId: UUID, @PathVariable followedId: UUID) {
        unfollowUserUseCase.unfollow(userId, followedId)
    }

    @GetMapping("/followers")
    fun getFollowers(@PathVariable userId: UUID): List<UUID> =
        getFollowersUseCase.getFollowers(userId)

    @GetMapping("/following")
    fun getFollowing(@PathVariable userId: UUID): List<UUID> =
        getFollowedUsersUseCase.getFollowedUsers(userId)
}
