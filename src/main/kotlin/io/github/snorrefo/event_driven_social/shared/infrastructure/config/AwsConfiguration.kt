package io.github.snorrefo.event_driven_social.shared.infrastructure.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sqs.SqsClient

@Configuration
@EnableScheduling
@EnableConfigurationProperties(AwsProperties::class)
class AwsConfiguration {

    @Bean
    fun snsClient(awsProperties: AwsProperties): SnsClient {
        return SnsClient.builder()
            .region(Region.of(awsProperties.region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }

    @Bean
    fun sqsClient(awsProperties: AwsProperties): SqsClient {
        return SqsClient.builder()
            .region(Region.of(awsProperties.region))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build()
    }
}
