package io.github.snorrefo.event_driven_social.timeline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TimelineServiceApplication

fun main(args: Array<String>) {
    runApplication<TimelineServiceApplication>(*args)
}
