package io.github.snorrefo.event_driven_social.domain.web.controller


import com.fasterxml.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.domain.model.User
import io.github.snorrefo.event_driven_social.domain.service.UserService
import io.github.snorrefo.event_driven_social.web.dto.request.CreateUserRequest
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
    private lateinit var userService: UserService

    @Test
    fun `should create user`() {
        // Given
        val request = CreateUserRequest(username = "john.doe", displayName = "John Doe")

        val user = User(
            id = UUID.randomUUID(),
            username = request.username,
            displayName = request.displayName
        )

        `when`(userService.createUser(request.username, request.displayName))
            .thenReturn(user)


        // When/Then
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.username").value("john.doe"))
            .andExpect(jsonPath("$.displayName").value("John Doe"))

        verify(userService, times(1)).createUser(request.username, request.displayName)
    }

    @Test
    fun `should create user with bio and avatar`() {
        // Given
        val request = CreateUserRequest(username = "janedoe", displayName = "Jane Doe")

        val user = User(
            id = UUID.randomUUID(),
            username = request.username,
            displayName = request.displayName,
            bio = "Software developer",
            avatarUrl = "https://www.example.com/image.jpg"
        )



        `when`(userService.createUser(request.username, request.displayName, request.bio, request.avatarUrl))
            .thenReturn(user)

        // When/Then
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(jsonPath("$.username").value("janedoe"))
            .andExpect(jsonPath("$.displayName").value("Jane Doe"))
            .andExpect(jsonPath("$.bio").value("Software developer"))
            .andExpect(jsonPath("$.avatarUrl").value("https://www.example.com/image.jpg"))

        verify(userService, times(1)).createUser(request.username, request.displayName, request.bio, request.avatarUrl)
    }

    @Test
    fun `should reject user with blank username`() {
        // Given
        val request = CreateUserRequest(username = "     ", displayName = "John Doe")

        val user = User(
            id = UUID.randomUUID(),
            username = request.username,
            displayName = request.displayName
        )

        `when`(userService.createUser(request.username, request.displayName))
            .thenReturn(user)


        // When/Then
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(userService)
    }

    @Test
    fun `should reject user with short username`() {
        // Given
        val request = CreateUserRequest(username = "ab", displayName = "John Doe")

        val user = User(
            id = UUID.randomUUID(),
            username = request.username,
            displayName = request.displayName
        )

        `when`(userService.createUser(request.username, request.displayName))
            .thenReturn(user)


        // When/Then
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(userService)
    }

//    @Test
//    fun `should reject user with long username`() {
//        // Given
//        val userId = UUID.randomUUID()
//        val request = CreateUserRequest(content = "   ")
//
//        // When/Then
//        mockMvc.perform(
//            user("/api/users")
//                .header("X-User-Id", userId.toString())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request))
//        )
//            .andExpect(status().isBadRequest)
//            .andExpect(jsonPath("$.errors").exists())
//
//        verifyNoInteractions(userService)
//    }
//
//
//    @Test
//    fun `should get user by id`() {
//        // Given
//        val userId = UUID.randomUUID()
//        val user = User(
//            id = userId,
//            authorId = UUID.randomUUID(),
//            content = "Test user"
//        )
//
//        `when`(userService.getUser(userId)).thenReturn(user)
//
//        // When/Then
//        mockMvc.perform(get("/api/users/{userId}", userId))
//            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.id").value(userId.toString()))
//            .andExpect(jsonPath("$.content").value("Test user"))
//
//        verify(userService, times(1)).getUser(userId)
//    }
//
//    @Test
//    fun `should return 404 when user not found`() {
//        // Given
//        val userId = UUID.randomUUID()
//
//        `when`(userService.getUser(userId)).thenReturn(null)
//
//        // When/Then
//        mockMvc.perform(get("/api/users/{userId}", userId))
//            .andExpect(status().isNotFound)
//    }
//
//    @Test
//    fun `should get user by username`() {
//        // Given
//        val userId = UUID.randomUUID()
//        val user = User(
//            id = userId,
//            authorId = UUID.randomUUID(),
//            content = "Test user"
//        )
//
//        `when`(userService.getUser(userId)).thenReturn(user)
//
//        // When/Then
//        mockMvc.perform(get("/api/users/{userId}", userId))
//            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.id").value(userId.toString()))
//            .andExpect(jsonPath("$.content").value("Test user"))
//
//        verify(userService, times(1)).getUser(userId)
//    }
//
//    @Test
//    fun `should return 404 when username not found`() {
//        // Given
//        val userId = UUID.randomUUID()
//        val user = User(
//            id = userId,
//            authorId = UUID.randomUUID(),
//            content = "Test user"
//        )
//
//        `when`(userService.getUser(userId)).thenReturn(user)
//
//        // When/Then
//        mockMvc.perform(get("/api/users/{userId}", userId))
//            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.id").value(userId.toString()))
//            .andExpect(jsonPath("$.content").value("Test user"))
//
//        verify(userService, times(1)).getUser(userId)
//    }
}