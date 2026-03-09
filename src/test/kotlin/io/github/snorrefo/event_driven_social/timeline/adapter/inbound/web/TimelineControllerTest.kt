package io.github.snorrefo.event_driven_social.timeline.adapter.inbound.web

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import io.github.snorrefo.event_driven_social.timeline.domain.port.inbound.GetTimelineUseCase
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TimelineControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getTimelineUseCase: GetTimelineUseCase

    @Test
    fun `should return timeline entries`() {
        val userId = UUID.randomUUID()
        val entries = listOf(
            TimelineEntry(
                postId = UUID.randomUUID(),
                authorId = UUID.randomUUID(),
                content = "Hello timeline!",
                createdAt = Instant.parse("2026-03-14T10:00:00Z")
            ),
            TimelineEntry(
                postId = UUID.randomUUID(),
                authorId = UUID.randomUUID(),
                content = "Another post",
                createdAt = Instant.parse("2026-03-14T09:00:00Z"),
                mediaUrls = listOf("https://example.com/photo.jpg")
            )
        )

        `when`(getTimelineUseCase.getTimeline(userId, 0, 20)).thenReturn(entries)

        mockMvc.perform(
            get("/api/timeline")
                .header("X-User-Id", userId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].content").value("Hello timeline!"))
            .andExpect(jsonPath("$[0].postId").exists())
            .andExpect(jsonPath("$[0].authorId").exists())
            .andExpect(jsonPath("$[1].mediaUrls.length()").value(1))
    }

    @Test
    fun `should support custom pagination parameters`() {
        val userId = UUID.randomUUID()

        `when`(getTimelineUseCase.getTimeline(userId, 2, 10)).thenReturn(emptyList())

        mockMvc.perform(
            get("/api/timeline")
                .header("X-User-Id", userId.toString())
                .param("page", "2")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))

        verify(getTimelineUseCase, times(1)).getTimeline(userId, 2, 10)
    }

    @Test
    fun `should return empty list when no timeline entries`() {
        val userId = UUID.randomUUID()

        `when`(getTimelineUseCase.getTimeline(userId, 0, 20)).thenReturn(emptyList())

        mockMvc.perform(
            get("/api/timeline")
                .header("X-User-Id", userId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    fun `should return 400 when X-User-Id header is missing`() {
        mockMvc.perform(get("/api/timeline"))
            .andExpect(status().isBadRequest)
    }
}
