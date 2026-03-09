package io.github.snorrefo.event_driven_social.post.adapter.inbound.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.snorrefo.event_driven_social.post.adapter.inbound.web.dto.CreatePostRequest
import io.github.snorrefo.event_driven_social.post.domain.model.Post
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.CreatePostUseCase
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.GetPostUseCase
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.GetPostsByAuthorsUseCase
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
    private lateinit var createPostUseCase: CreatePostUseCase

    @MockitoBean
    private lateinit var getPostUseCase: GetPostUseCase

    @MockitoBean
    private lateinit var getPostsByAuthorsUseCase: GetPostsByAuthorsUseCase

    @Test
    fun `should create post`() {
        val userId = UUID.randomUUID()
        val request = CreatePostRequest(content = "Hello, world!")

        val post = Post(id = UUID.randomUUID(), authorId = userId, content = request.content)

        `when`(createPostUseCase.createPost(userId, request.content, null, emptyList()))
            .thenReturn(post)

        mockMvc.perform(
            post("/api/posts")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.content").value("Hello, world!"))
            .andExpect(jsonPath("$.authorId").value(userId.toString()))

        verify(createPostUseCase, times(1)).createPost(userId, request.content, null, emptyList())
    }

    @Test
    fun `should create post with media URLs`() {
        val userId = UUID.randomUUID()
        val mediaUrls = listOf("https://example.com/photo.jpg")
        val request = CreatePostRequest(content = "Check this out!", mediaUrls = mediaUrls)

        val post = Post(id = UUID.randomUUID(), authorId = userId, content = request.content, mediaUrls = mediaUrls)

        `when`(createPostUseCase.createPost(userId, request.content, null, mediaUrls))
            .thenReturn(post)

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
        val userId = UUID.randomUUID()
        val originalPostId = UUID.randomUUID()
        val request = CreatePostRequest(content = "Great point!", inReplyToPostId = originalPostId.toString())

        val post = Post(id = UUID.randomUUID(), authorId = userId, content = request.content, inReplyToPostId = originalPostId)

        `when`(createPostUseCase.createPost(userId, request.content, originalPostId, emptyList()))
            .thenReturn(post)

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
        val userId = UUID.randomUUID()
        val request = CreatePostRequest(content = "   ")

        mockMvc.perform(
            post("/api/posts")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.errors").exists())

        verifyNoInteractions(createPostUseCase)
    }

    @Test
    fun `should reject post exceeding 280 characters`() {
        val userId = UUID.randomUUID()
        val request = CreatePostRequest(content = "a".repeat(281))

        mockMvc.perform(
            post("/api/posts")
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        verifyNoInteractions(createPostUseCase)
    }

    @Test
    fun `should get post by id`() {
        val postId = UUID.randomUUID()
        val post = Post(id = postId, authorId = UUID.randomUUID(), content = "Test post")

        `when`(getPostUseCase.getPost(postId)).thenReturn(post)

        mockMvc.perform(get("/api/posts/{postId}", postId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(postId.toString()))
            .andExpect(jsonPath("$.content").value("Test post"))

        verify(getPostUseCase, times(1)).getPost(postId)
    }

    @Test
    fun `should return 404 when post not found`() {
        val postId = UUID.randomUUID()

        `when`(getPostUseCase.getPost(postId)).thenReturn(null)

        mockMvc.perform(get("/api/posts/{postId}", postId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should get user posts with default pagination`() {
        val userId = UUID.randomUUID()
        val posts = listOf(
            Post(id = UUID.randomUUID(), authorId = userId, content = "Post 1"),
            Post(id = UUID.randomUUID(), authorId = userId, content = "Post 2")
        )

        `when`(getPostUseCase.getUserPosts(userId, 0, 20)).thenReturn(posts)

        mockMvc.perform(get("/api/posts/user/{userId}", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].content").value("Post 1"))
            .andExpect(jsonPath("$[1].content").value("Post 2"))
    }

    @Test
    fun `should support custom pagination parameters`() {
        val userId = UUID.randomUUID()
        val posts = listOf(Post(id = UUID.randomUUID(), authorId = userId, content = "Post on page 2"))

        `when`(getPostUseCase.getUserPosts(userId, 1, 10)).thenReturn(posts)

        mockMvc.perform(
            get("/api/posts/user/{userId}", userId)
                .param("page", "1")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))

        verify(getPostUseCase, times(1)).getUserPosts(userId, 1, 10)
    }

    @Test
    fun `should return empty list when user has no posts`() {
        val userId = UUID.randomUUID()

        `when`(getPostUseCase.getUserPosts(userId, 0, 20)).thenReturn(emptyList())

        mockMvc.perform(get("/api/posts/user/{userId}", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }
}
