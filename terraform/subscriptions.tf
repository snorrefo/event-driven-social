# Notification events queue subscribes to posts-created and users-followed
resource "aws_sns_topic_subscription" "notification_posts_created" {
  topic_arn = aws_sns_topic.posts_created.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.notification_events.arn

  raw_message_delivery = false
}

resource "aws_sns_topic_subscription" "notification_users_followed" {
  topic_arn = aws_sns_topic.users_followed.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.notification_events.arn

  raw_message_delivery = false
}

# User count events queue subscribes to posts-created and users-followed
resource "aws_sns_topic_subscription" "user_count_posts_created" {
  topic_arn = aws_sns_topic.posts_created.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.user_count_events.arn

  raw_message_delivery = false
}

resource "aws_sns_topic_subscription" "user_count_users_followed" {
  topic_arn = aws_sns_topic.users_followed.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.user_count_events.arn

  raw_message_delivery = false
}

# Timeline events queue subscribes to posts-created
resource "aws_sns_topic_subscription" "timeline_posts_created" {
  topic_arn = aws_sns_topic.posts_created.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.timeline_events.arn

  raw_message_delivery = false
}
