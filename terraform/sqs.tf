# --- Notification Events ---

resource "aws_sqs_queue" "notification_events_dlq" {
  name                      = "event-driven-social-${var.environment}-notification-events-dlq"
  message_retention_seconds = 1209600 # 14 days
}

resource "aws_sqs_queue" "notification_events" {
  name                       = "event-driven-social-${var.environment}-notification-events"
  visibility_timeout_seconds = 30
  message_retention_seconds  = 345600 # 4 days

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.notification_events_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sqs_queue_policy" "notification_events" {
  queue_url = aws_sqs_queue.notification_events.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = { Service = "sns.amazonaws.com" }
        Action    = "sqs:SendMessage"
        Resource  = aws_sqs_queue.notification_events.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = [
              aws_sns_topic.posts_created.arn,
              aws_sns_topic.users_followed.arn
            ]
          }
        }
      }
    ]
  })
}

# --- User Count Events ---

resource "aws_sqs_queue" "user_count_events_dlq" {
  name                      = "event-driven-social-${var.environment}-user-count-events-dlq"
  message_retention_seconds = 1209600
}

resource "aws_sqs_queue" "user_count_events" {
  name                       = "event-driven-social-${var.environment}-user-count-events"
  visibility_timeout_seconds = 30
  message_retention_seconds  = 345600

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.user_count_events_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sqs_queue_policy" "user_count_events" {
  queue_url = aws_sqs_queue.user_count_events.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = { Service = "sns.amazonaws.com" }
        Action    = "sqs:SendMessage"
        Resource  = aws_sqs_queue.user_count_events.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = [
              aws_sns_topic.posts_created.arn,
              aws_sns_topic.users_followed.arn
            ]
          }
        }
      }
    ]
  })
}

# --- Timeline Events ---

resource "aws_sqs_queue" "timeline_events_dlq" {
  name                      = "event-driven-social-${var.environment}-timeline-events-dlq"
  message_retention_seconds = 1209600
}

resource "aws_sqs_queue" "timeline_events" {
  name                       = "event-driven-social-${var.environment}-timeline-events"
  visibility_timeout_seconds = 30
  message_retention_seconds  = 345600

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.timeline_events_dlq.arn
    maxReceiveCount     = 3
  })
}

resource "aws_sqs_queue_policy" "timeline_events" {
  queue_url = aws_sqs_queue.timeline_events.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = { Service = "sns.amazonaws.com" }
        Action    = "sqs:SendMessage"
        Resource  = aws_sqs_queue.timeline_events.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.posts_created.arn
          }
        }
      }
    ]
  })
}
