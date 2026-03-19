resource "aws_sns_topic_subscription" "timeline_posts_created" {
  topic_arn            = aws_sns_topic.posts_created.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.timeline_events.arn
  raw_message_delivery = false
}

resource "aws_sns_topic_subscription" "timeline_users_followed" {
  topic_arn            = aws_sns_topic.users_followed.arn
  protocol             = "sqs"
  endpoint             = aws_sqs_queue.timeline_events.arn
  raw_message_delivery = false
}

resource "aws_sqs_queue_policy" "timeline_events_policy" {
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
            "aws:SourceArn" = [
              aws_sns_topic.posts_created.arn,
              aws_sns_topic.users_followed.arn,
            ]
          }
        }
      }
    ]
  })
}
