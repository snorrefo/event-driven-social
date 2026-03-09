# SNS Topic ARNs
output "posts_created_topic_arn" {
  description = "ARN of the posts-created SNS topic"
  value       = aws_sns_topic.posts_created.arn
}

output "users_followed_topic_arn" {
  description = "ARN of the users-followed SNS topic"
  value       = aws_sns_topic.users_followed.arn
}

output "posts_liked_topic_arn" {
  description = "ARN of the posts-liked SNS topic"
  value       = aws_sns_topic.posts_liked.arn
}

# SQS Queue URLs
output "notification_events_queue_url" {
  description = "URL of the notification-events SQS queue"
  value       = aws_sqs_queue.notification_events.url
}

output "user_count_events_queue_url" {
  description = "URL of the user-count-events SQS queue"
  value       = aws_sqs_queue.user_count_events.url
}

output "timeline_events_queue_url" {
  description = "URL of the timeline-events SQS queue"
  value       = aws_sqs_queue.timeline_events.url
}

# DLQ ARNs (for monitoring)
output "notification_events_dlq_arn" {
  description = "ARN of the notification-events dead-letter queue"
  value       = aws_sqs_queue.notification_events_dlq.arn
}

output "user_count_events_dlq_arn" {
  description = "ARN of the user-count-events dead-letter queue"
  value       = aws_sqs_queue.user_count_events_dlq.arn
}

output "timeline_events_dlq_arn" {
  description = "ARN of the timeline-events dead-letter queue"
  value       = aws_sqs_queue.timeline_events_dlq.arn
}
