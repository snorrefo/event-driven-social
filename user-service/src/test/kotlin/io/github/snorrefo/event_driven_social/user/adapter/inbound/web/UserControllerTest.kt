package io.github.snorrefo.event_driven_social.user.adapter.inbound.web

import io.github.snorrefo.event_driven_social.user.domain.model.User
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.CreateUserUseCase
import io.github.snorrefo.event_driven_social.user.domain.port.inbound.GetUserUseCase
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID

@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var createUserUseCase: CreateUserUseCase

    @MockitoBean
    lateinit var getUserUseCase: GetUserUseCase

    @Test
    fun `POST creates user and returns 201`() {
        val user = User(username = "alice", displayName = "Alice")
        whenever(createUserUseCase.createUser(any(), any())).thenReturn(user)

        mockMvc.post("/api/users") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"username": "alice", "displayName": "Alice"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.username") { value("alice") }
        }
    }

    @Test
    fun `GET returns user by ID`() {
        val id = UUID.randomUUID()
        val user = User(id = id, username = "alice", displayName = "Alice")
        whenever(getUserUseCase.getUser(id)).thenReturn(user)

        mockMvc.get("/api/users/$id")
            .andExpect {
                status { isOk() }
                jsonPath("$.username") { value("alice") }
            }
    }

    @Test
    fun `GET returns 404 for unknown user`() {
        val id = UUID.randomUUID()
        whenever(getUserUseCase.getUser(id)).thenThrow(NoSuchElementException("User not found"))

        mockMvc.get("/api/users/$id")
            .andExpect {
                status { isNotFound() }
            }
    }
}
