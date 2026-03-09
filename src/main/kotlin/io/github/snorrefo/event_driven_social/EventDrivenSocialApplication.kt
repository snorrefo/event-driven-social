package io.github.snorrefo.event_driven_social

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventDrivenSocialApplication

fun main(args: Array<String>) {
    runApplication<EventDrivenSocialApplication>(*args)
}