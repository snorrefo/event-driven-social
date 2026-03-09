resource "aws_sns_topic" "posts_created" {
  name = "event-driven-social-${var.environment}-posts-created"
}

resource "aws_sns_topic" "users_followed" {
  name = "event-driven-social-${var.environment}-users-followed"
}

resource "aws_sns_topic" "posts_liked" {
  name = "event-driven-social-${var.environment}-posts-liked"
}
