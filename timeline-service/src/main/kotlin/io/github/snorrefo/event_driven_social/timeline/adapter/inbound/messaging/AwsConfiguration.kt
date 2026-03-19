package io.github.snorrefo.event_driven_social.timeline.adapter.inbound.messaging

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
class AwsConfiguration(
    @Value("\${app.aws.region}") private val region: String,
) {

    @Bean
    fun sqsClient(): SqsClient = SqsClient.builder()
        .region(Region.of(region))
        .build()
}
