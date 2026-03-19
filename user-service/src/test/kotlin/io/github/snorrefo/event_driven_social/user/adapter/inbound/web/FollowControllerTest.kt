package io.github.snorrefo.event_driven_social.user.adapter.inbound.web

import io.github.snorrefo.event_driven_social.user.domain.port.inbound.FollowUserUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.GetFollowedUsersUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.GetFollowersUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.UnfollowUserUseCase
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest(FollowController::class)
class FollowControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var followUserUseCase: FollowUserUseCase

    @MockitoBean
    lateinit var unfollowUserUseCase: UnfollowUserUseCase

    @MockitoBean
    lateinit var getFollowersUseCase: GetFollowersUseCase

    @MockitoBean
    lateinit var getFollowedUsersUseCase: GetFollowedUsersUseCase

    @Test
    fun `POST follow returns 201`() {
        val userId = UUID.randomUUID()
        val followedId = UUID.randomUUID()

        mockMvc.post("/api/users/$userId/following") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"followedId": "$followedId"}"""
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `GET followers returns list`() {
        val userId = UUID.randomUUID()
        val followers = listOf(UUID.randomUUID(), UUID.randomUUID())
        whenever(getFollowersUseCase.getFollowers(userId)).thenReturn(followers)

        mockMvc.get("/api/users/$userId/followers")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(2) }
            }
    }
}
