package io.github.snorrefo.event_driven_social.shared.infrastructure.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.sqs.SqsClient

abstract class SqsMessagePoller(
    private val sqsClient: SqsClient,
    private val queueUrl: String,
    protected val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    open fun pollAndProcess() {
        val response = sqsClient.receiveMessage {
            it.queueUrl(queueUrl).maxNumberOfMessages(10).waitTimeSeconds(5)
        }
        for (message in response.messages()) {
            try {
                val snsEnvelope = objectMapper.readTree(message.body())
                val eventPayload = snsEnvelope.get("Message").asText()
                val eventType = snsEnvelope.at("/MessageAttributes/eventType/Value").asText()
                handleMessage(eventType, eventPayload)
                sqsClient.deleteMessage { it.queueUrl(queueUrl).receiptHandle(message.receiptHandle()) }
            } catch (e: Exception) {
                logger.error("Failed to process SQS message: ${message.messageId()}", e)
            }
        }
    }

    abstract fun handleMessage(eventType: String, payload: String)
}
