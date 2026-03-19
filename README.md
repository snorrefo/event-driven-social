# Event-Driven Social

A Twitter-like microservices demo built with Kotlin, Spring Boot 4, and AWS messaging. Three independent services
communicate through events — no direct service-to-service calls.

## What does it do?

Users can create accounts, follow each other, and write posts. When someone you follow writes a post, it automatically
appears in your timeline. All of this happens asynchronously through events:

1. **User Service** — manages user accounts and follow relationships. When you follow someone, it publishes a
   `UserFollowedEvent`.
2. **Post Service** — manages posts. When you create a post, it publishes a `PostCreatedEvent`.
3. **Timeline Service** — listens for those events and builds a personalized timeline for each user. It stores timelines
   in Redis for fast reads.

No service calls another directly. They communicate by publishing events to AWS SNS topics, which get delivered to an
SQS queue that the Timeline Service polls.

## Architecture overview

```
┌──────────────┐         ┌──────────────┐
│ User Service │         │ Post Service  │
│ (PostgreSQL) │         │ (PostgreSQL)  │
└──────┬───────┘         └──────┬────────┘
       │ SNS: user.followed        │ SNS: post.created
       │ SNS: user.unfollowed      │
       └──────────┬────────────────┘
                  │ SQS: timeline-events
                  ▼
        ┌─────────────────┐
        │ Timeline Service │
        │    (Redis)       │
        └──────────────────┘
```

Each service follows **hexagonal architecture** (also called ports & adapters) — business logic in the center,
infrastructure details (database, messaging, HTTP) on the outside.

Events are published using the **transactional outbox pattern**: the event is written to an outbox table in the same
database transaction as the business data, then a Quartz job polls the outbox and publishes to SNS. This guarantees no
events are lost even if SNS is temporarily unavailable.

## Prerequisites

- **JDK 21** (the project uses Gradle toolchains, so it will download this automatically if needed)
- **Docker** — required for local databases (PostgreSQL + Redis) and for running tests (Testcontainers)
- **AWS account** — the services publish/consume real SNS/SQS (
  see [Terraform setup](#2-set-up-aws-resources-using-terraform))

## Getting started

### 1. Start local infrastructure

```bash
docker compose up -d
```

This starts two PostgreSQL instances (ports 5432 and 5433) and one Redis instance (port 6379).

### 2. Set up AWS credentials

The services publish to real SNS topics and consume from a real SQS queue. You need an AWS account and credentials with the right permissions.

#### Create an IAM user for local development

In the AWS console, go to **IAM → Users → Create user**. Give it a name like `event-driven-social-dev`, then attach an inline policy with exactly the permissions these services need:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublishToSns",
      "Effect": "Allow",
      "Action": "sns:Publish",
      "Resource": [
        "arn:aws:sns:eu-north-1:YOUR_ACCOUNT_ID:dev-posts-created",
        "arn:aws:sns:eu-north-1:YOUR_ACCOUNT_ID:dev-users-followed"
      ]
    },
    {
      "Sid": "ConsumeFromSqs",
      "Effect": "Allow",
      "Action": [
        "sqs:ReceiveMessage",
        "sqs:DeleteMessage",
        "sqs:GetQueueAttributes"
      ],
      "Resource": "arn:aws:sqs:eu-north-1:YOUR_ACCOUNT_ID:dev-timeline-events"
    }
  ]
}
```

Replace `YOUR_ACCOUNT_ID` and adjust the region if you change `aws_region` in Terraform.

Then create an **access key** for the user (IAM → User → Security credentials → Create access key). Configure your local CLI:

```bash
aws configure --profile event-driven-social
# Enter: Access Key ID, Secret Access Key, region (eu-north-1), output format (json)
```

Set the profile in your shell before running the services:

```bash
export AWS_PROFILE=event-driven-social
```

> **Note:** The Terraform step below also needs AWS credentials. The IAM user above only has SNS/SQS permissions, so either use your admin credentials for Terraform, or add IAM + SNS + SQS write permissions to a separate Terraform user.

#### Apply Terraform to create the AWS resources

```bash
cd terraform
terraform init
terraform apply
```

### 3. Build everything

```bash
./gradlew build
```

This compiles all modules and runs the full test suite. Tests use Testcontainers, so Docker must be running.

### 4. Run the services

Each service has a `dev` profile for local development:

```bash
./gradlew :user-service:bootRun --args='--spring.profiles.active=dev'
./gradlew :post-service:bootRun --args='--spring.profiles.active=dev'
./gradlew :timeline-service:bootRun --args='--spring.profiles.active=dev'
```

Run each command in a separate terminal.

### 5. Try it out

```bash
# Create a user
curl -s -X POST http://localhost:8081/api/users \
  -H 'Content-Type: application/json' \
  -d '{"username": "alice", "displayName": "Alice"}' | jq

# Create another user
curl -s -X POST http://localhost:8081/api/users \
  -H 'Content-Type: application/json' \
  -d '{"username": "bob", "displayName": "Bob"}' | jq

# Alice follows Bob (use the UUIDs from the responses above)
curl -s -X POST http://localhost:8081/api/users/{aliceId}/following \
  -H 'Content-Type: application/json' \
  -d '{"followedId": "{bobId}"}'

# Bob writes a post
curl -s -X POST http://localhost:8082/api/posts \
  -H 'Content-Type: application/json' \
  -d '{"authorId": "{bobId}", "content": "Hello world!"}' | jq

# Wait a few seconds for events to propagate, then check Alice's timeline
curl -s http://localhost:8083/api/timelines/{aliceId} | jq
```

## Project structure

```
event-driven-social/
├── shared/              # Plain Kotlin library — domain events & port interfaces
├── user-service/        # Spring Boot app — users & social graph (PostgreSQL)
├── post-service/        # Spring Boot app — posts (PostgreSQL)
├── timeline-service/    # Spring Boot app — timelines (Redis)
├── docker-compose.yml   # Local PostgreSQL + Redis
└── terraform/           # AWS SNS/SQS infrastructure
```

## Running tests

```bash
# All tests
./gradlew test

# Single module
./gradlew :user-service:test

# Single test class
./gradlew :post-service:test --tests "io.github.snorrefo.event_driven_social.post.application.PostApplicationServiceTest"

# Test coverage report (opens in build/reports/kover/html/)
./gradlew koverHtmlReport
```

## Docker images

Each service has a multi-stage Dockerfile:

```bash
docker build -f user-service/Dockerfile -t event-driven-social/user-service .
docker build -f post-service/Dockerfile -t event-driven-social/post-service .
docker build -f timeline-service/Dockerfile -t event-driven-social/timeline-service .
```

Services expose ports 8081, 8082, and 8083 respectively.

## CI

GitHub Actions runs `./gradlew build` on every push and pull request to `main`. See `.github/workflows/ci.yml`.

## Tech stack

|                   |                                                   |
|-------------------|---------------------------------------------------|
| Language          | Kotlin                                            |
| Framework         | Spring Boot 4, Spring Data JPA, Spring Data Redis |
| Databases         | PostgreSQL (users, posts), Redis (timelines)      |
| Migrations        | Flyway                                            |
| Messaging         | AWS SNS + SQS                                     |
| Event reliability | Transactional outbox pattern with Quartz          |
| Testing           | JUnit 5, Mockito-Kotlin, Testcontainers           |
| Build             | Gradle (multi-module), Docker                     |
| CI                | GitHub Actions                                    |
| IaC               | Terraform                                         |

## Key patterns demonstrated

- **Event-driven architecture** — services communicate asynchronously through domain events
- **Hexagonal architecture** — domain logic is independent of frameworks and infrastructure
- **Transactional outbox** — events are guaranteed to be published exactly once
- **CQRS-lite** — posts are written to PostgreSQL, timelines are read from Redis
- **Materialized view** — the Timeline Service builds and maintains its own read-optimized projection

---

<details>
<summary>Original project notes (planning reference)</summary>

> Build One Real Project That Demonstrates Modern Patterns
>
> Event-Driven Microservices Project (small twitter clone):
>
> 3 small services (User Service, Post Service, Timeline Service)
> Event-driven communication via SQS/SNS (no localstack)
> Hexagonal/Clean Architecture in each service
> Containerized with Docker, deployed to ECS/Fargate
> IaC with Terraform or CDK
> Simple CI/CD pipeline in GitHub Actions
>
> Main stack:
> Kotlin
> Spring Boot 4, Spring Data JPA, Spring Validation, Quartz
> PostgreSQL with Flyway migrations for User and Post Services
> Redis/DynamoDB for Timeline Service
> JUnit 5 + Testcontainers for PostgreSQL in tests (no external DB needed for tests)
>
> This single project addresses: cloud deployment, modern architecture patterns, event-driven design, and container
> orchestration. Make the GitHub repo pristine - treat it like your portfolio piece.
>
> What I'd Recommend for Your Project
>
> Database-backed microservices with event-driven communication:
> Post Service:
> ├── PostgreSQL (stores posts), transactional outbox pattern for publishing events
> └── Publishes to: posts.created topic
>
> Timeline Service:
> ├── Redis/DynamoDB (materialized timelines)
> └── Subscribes to: posts.created, user.followed
>
> User Service:
> ├── PostgreSQL (user profiles, follows) transactional outbox pattern for publishing events
> └── Publishes to: user.followed, user.unfollowed
>
> Events flow through SNS, but each service has a database for its state.
> This demonstrates:
> ✓ Event-driven architecture (addresses your gap)
> ✓ Service isolation with separate databases (microservices pattern)
> ✓ Async communication (modern distributed systems)
> ✓ CQRS-lite (Timeline Service is optimized for reads, separate from Post writes)
> ✓ Practical, maintainable architecture (what companies actually want)

</details>
