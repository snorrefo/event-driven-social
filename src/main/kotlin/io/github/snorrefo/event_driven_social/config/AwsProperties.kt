package io.github.snorrefo.event_driven_social.config


import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws")
data class AwsProperties(
    val region: String,
    val sns: SnsTopics,
    val sqs: SqsQueues
)

data class SnsTopics(
    val postsCreated: String,
    val usersFollowed: String,
    val postsLiked: String
)

data class SqsQueues(
    val timelineEvents: String,
    val notificationEvents: String
)