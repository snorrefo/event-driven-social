package io.github.snorrefo.event_driven_social.timeline.adapter.inbound.web

import io.github.snorrefo.event_driven_social.timeline.domain.model.TimelineEntry
import io.github.snorrefo.event_driven_social.timeline.domain.port.inbound.GetTimelineUseCase
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Instant
import java.util.*

@WebMvcTest(
    TimelineController::class, properties = [
        "app.aws.region=eu-north-1",
        "app.sqs.timeline-events-queue-url=http://localhost:4566/queue/test",
    ]
)
class TimelineControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var getTimelineUseCase: GetTimelineUseCase

    @Test
    fun `GET timeline returns entries`() {
        val userId = UUID.randomUUID()
        val entries = listOf(
            TimelineEntry(UUID.randomUUID(), UUID.randomUUID(), "Hello", Instant.now()),
            TimelineEntry(UUID.randomUUID(), UUID.randomUUID(), "World", Instant.now()),
        )
        whenever(getTimelineUseCase.getTimeline(userId, 0, 20)).thenReturn(entries)

        mockMvc.get("/api/timelines/$userId")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(2) }
                jsonPath("$[0].content") { value("Hello") }
            }
    }

    @Test
    fun `GET timeline with pagination`() {
        val userId = UUID.randomUUID()
        whenever(getTimelineUseCase.getTimeline(userId, 5, 10)).thenReturn(emptyList())

        mockMvc.get("/api/timelines/$userId?offset=5&limit=10")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(0) }
            }
    }
}
