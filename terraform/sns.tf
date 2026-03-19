resource "aws_sns_topic" "posts_created" {
  name = "${var.environment}-posts-created"
}

resource "aws_sns_topic" "users_followed" {
  name = "${var.environment}-users-followed"
}
