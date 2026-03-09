package io.github.snorrefo.event_driven_social.identity.adapter.inbound.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.identity.adapter.inbound.web.dto.CreateUserRequest
import io.github.snorrefo.event_driven_social.identity.domain.model.User
import io.github.snorrefo.event_driven_social.identity.domain.port.inbound.CreateUserUseCase
import io.github.snorrefo.event_driven_social.identity.domain.port.inbound.GetUserUseCase
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var createUserUseCase: CreateUserUseCase

    @MockitoBean
    private lateinit var getUserUseCase: GetUserUseCase

    @Test
    fun `should create user`() {
        val request = CreateUserRequest(username = "john.doe", displayName = "John Doe")

        val user = User(id = UUID.randomUUID(), username = request.username, displayName = request.displayName)

        `when`(createUserUseCase.createUser(request.username, request.displayName))
            .thenReturn(user)

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.username").value("john.doe"))
            .andExpect(jsonPath("$.displayName").value("John Doe"))

        verify(createUserUseCase, times(1)).createUser(request.username, request.displayName)
    }

    @Test
    fun `should create user with bio and avatar`() {
        val request = CreateUserRequest(username = "janedoe", displayName = "Jane Doe")

        val user = User(
            id = UUID.randomUUID(),
            username = request.username,
            displayName = request.displayName,
            bio = "Software developer",
            avatarUrl = "https://www.example.com/image.jpg"
        )

        `when`(createUserUseCase.createUser(request.username, request.displayName, request.bio, request.avatarUrl))
            .thenReturn(user)

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(jsonPath("$.username").value("janedoe"))
            .andExpect(jsonPath("$.displayName").value("Jane Doe"))
            .andExpect(jsonPath("$.bio").value("Software developer"))
            .andExpect(jsonPath("$.avatarUrl").value("https://www.example.com/image.jpg"))

        verify(createUserUseCase, times(1)).createUser(request.username, request.displayName, request.bio, request.avatarUrl)
    }

    @Test
    fun `should reject user with blank username`() {
        val request = CreateUserRequest(username = "     ", displayName = "John Doe")

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(createUserUseCase)
    }

    @Test
    fun `should reject user with short username`() {
        val request = CreateUserRequest(username = "ab", displayName = "John Doe")

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(createUserUseCase)
    }
}
