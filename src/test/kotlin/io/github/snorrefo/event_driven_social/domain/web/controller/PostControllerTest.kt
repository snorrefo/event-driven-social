package io.github.snorrefo.event_driven_social.domain.web.controller


import com.fasterxml.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.domain.model.Post
import io.github.snorrefo.event_driven_social.domain.service.PostService
import io.github.snorrefo.event_driven_social.web.dto.request.CreatePostRequest
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var postService: PostService

    @Test
    fun `should create post`() {
        // Given
        val userId = UUID.randomUUID()
        val request = CreatePostRequest(content = "Hello, world!")

        val post = Post(
            id = UUID.randomUUID(),
            authorId = userId,
            content = request.content
        )

        `when`(postService.createPost(userId, request.content, null, emptyList()))
            .thenReturn(post)


        // When/Then
        mockMvc.perform(
            post("/api/posts")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.content").value("Hello, world!"))
            .andExpect(jsonPath("$.authorId").value(userId.toString()))

        verify(postService, times(1)).createPost(userId, request.content, null, emptyList())
    }

    @Test
    fun `should create post with media URLs`() {
        // Given
        val userId = UUID.randomUUID()
        val mediaUrls = listOf("https://example.com/photo.jpg")
        val request = CreatePostRequest(
            content = "Check this out!",
            mediaUrls = mediaUrls
        )

        val post = Post(
            id = UUID.randomUUID(),
            authorId = userId,
            content = request.content,
            mediaUrls = mediaUrls
        )

        `when`(postService.createPost(userId, request.content, null, mediaUrls))
            .thenReturn(post)

        // When/Then
        mockMvc.perform(
            post("/api/posts")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.mediaUrls.length()").value(1))
    }

    @Test
    fun `should create reply post`() {
        // Given
        val userId = UUID.randomUUID()
        val originalPostId = UUID.randomUUID()
        val request = CreatePostRequest(
            content = "Great point!",
            inReplyToPostId = originalPostId.toString()
        )

        val post = Post(
            id = UUID.randomUUID(),
            authorId = userId,
            content = request.content,
            inReplyToPostId = originalPostId
        )

        `when`(postService.createPost(userId, request.content, originalPostId, emptyList()))
            .thenReturn(post)

        // When/Then
        mockMvc.perform(
            post("/api/posts")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.inReplyToPostId").value(originalPostId.toString()))
    }

    @Test
    fun `should reject post with blank content`() {
        // Given
        val userId = UUID.randomUUID()
        val request = CreatePostRequest(content = "   ")

        // When/Then
        mockMvc.perform(
            post("/api/posts")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors").exists())

        verifyNoInteractions(postService)
    }

    @Test
    fun `should reject post exceeding 280 characters`() {
        // Given
        val userId = UUID.randomUUID()
        val request = CreatePostRequest(content = "a".repeat(281))

        // When/Then
        mockMvc.perform(
            post("/api/posts")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(postService)
    }

    @Test
    fun `should get post by id`() {
        // Given
        val postId = UUID.randomUUID()
        val post = Post(
            id = postId,
            authorId = UUID.randomUUID(),
            content = "Test post"
        )

        `when`(postService.getPost(postId)).thenReturn(post)

        // When/Then
        mockMvc.perform(get("/api/posts/{postId}", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(postId.toString()))
            .andExpect(jsonPath("$.content").value("Test post"))

        verify(postService, times(1)).getPost(postId)
    }

    @Test
    fun `should return 404 when post not found`() {
        // Given
        val postId = UUID.randomUUID()

        `when`(postService.getPost(postId)).thenReturn(null)

        // When/Then
        mockMvc.perform(get("/api/posts/{postId}", postId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should get user posts with default pagination`() {
        // Given
        val userId = UUID.randomUUID()
        val posts = listOf(
            Post(id = UUID.randomUUID(), authorId = userId, content = "Post 1"),
            Post(id = UUID.randomUUID(), authorId = userId, content = "Post 2")
        )

        `when`(postService.getUserPosts(userId, 0, 20)).thenReturn(posts)

        // When/Then
        mockMvc.perform(get("/api/posts/user/{userId}", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].content").value("Post 1"))
            .andExpect(jsonPath("$[1].content").value("Post 2"))
    }

    @Test
    fun `should support custom pagination parameters`() {
        // Given
        val userId = UUID.randomUUID()
        val posts = listOf(
            Post(id = UUID.randomUUID(), authorId = userId, content = "Post on page 2")
        )

        `when`(postService.getUserPosts(userId, 1, 10)).thenReturn(posts)

        // When/Then
        mockMvc.perform(
            get("/api/posts/user/{userId}", userId)
                .param("page", "1")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))

        verify(postService, times(1)).getUserPosts(userId, 1, 10)
    }

    @Test
    fun `should return empty list when user has no posts`() {
        // Given
        val userId = UUID.randomUUID()

        `when`(postService.getUserPosts(userId, 0, 20)).thenReturn(emptyList())

        // When/Then
        mockMvc.perform(get("/api/posts/user/{userId}", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }
}