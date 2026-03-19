output "posts_created_topic_arn" {
  value = aws_sns_topic.posts_created.arn
}

output "users_followed_topic_arn" {
  value = aws_sns_topic.users_followed.arn
}

output "timeline_events_queue_url" {
  value = aws_sqs_queue.timeline_events.url
}

output "timeline_events_dlq_url" {
  value = aws_sqs_queue.timeline_events_dlq.url
}
