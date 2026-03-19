package io.github.snorrefo.event_driven_social.timeline

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    fun redisContainer() = com.redis.testcontainers.RedisContainer("redis:7-alpine")
}
