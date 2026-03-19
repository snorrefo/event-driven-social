package io.github.snorrefo.event_driven_social.post.adapter.inbound.web

import io.github.snorrefo.event_driven_social.post.domain.model.Post
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.CreatePostUseCase
import io.github.snorrefo.event_driven_social.post.domain.port.inbound.GetPostUseCase
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

@WebMvcTest(PostController::class)
class PostControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var createPostUseCase: CreatePostUseCase

    @MockitoBean
    lateinit var getPostUseCase: GetPostUseCase

    @Test
    fun `POST creates post and returns 201`() {
        val authorId = UUID.randomUUID()
        val post = Post(authorId = authorId, content = "Hello world")
        whenever(createPostUseCase.createPost(any(), any())).thenReturn(post)

        mockMvc.post("/api/posts") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"authorId": "$authorId", "content": "Hello world"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.content") { value("Hello world") }
        }
    }

    @Test
    fun `GET returns post by ID`() {
        val id = UUID.randomUUID()
        val post = Post(id = id, authorId = UUID.randomUUID(), content = "Hi")
        whenever(getPostUseCase.getPost(id)).thenReturn(post)

        mockMvc.get("/api/posts/$id")
            .andExpect {
                status { isOk() }
                jsonPath("$.content") { value("Hi") }
            }
    }
}
