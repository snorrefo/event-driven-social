package io.github.snorrefo.event_driven_social.post.adapter.outbound.messaging

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient

@Configuration
class AwsConfiguration(
    @Value("\${app.aws.region}") private val region: String,
) {

    @Bean
    fun snsClient(): SnsClient = SnsClient.builder()
        .region(Region.of(region))
        .build()
}
