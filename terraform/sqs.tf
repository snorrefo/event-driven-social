resource "aws_sqs_queue" "timeline_events_dlq" {
  name                      = "${var.environment}-timeline-events-dlq"
  message_retention_seconds = 1209600 # 14 days
}

resource "aws_sqs_queue" "timeline_events" {
  name                       = "${var.environment}-timeline-events"
  visibility_timeout_seconds = 30
  message_retention_seconds  = 345600 # 4 days
  receive_wait_time_seconds  = 5

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.timeline_events_dlq.arn
    maxReceiveCount     = 3
  })
}
