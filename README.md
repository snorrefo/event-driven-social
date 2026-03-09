Event-Driven Social Platform - Complete Project Structure
Technology Stack

Language: Kotlin 2.1.0
Framework: Spring Boot 4.0.2
Database: PostgreSQL 16
Messaging: AWS SNS/SQS
Infrastructure: Terraform (AWS)
Testing: JUnit 5, Testcontainers, MockK

This structure gives you:

✅ Clean hexagonal architecture (domain separate from infrastructure)
✅ Transactional outbox pattern for reliable event publishing
✅ AWS SNS/SQS integration ready
✅ Flyway database migrations
✅ Comprehensive testing setup
✅ Production-ready configuration

Start by implementing the Post and User services, then add the outbox publisher, then wire up AWS. Let me know which
part you want to tackle first!

Is this a modular monolith or event driven architecture?

Great question - this is both, and that's actually intentional. Let me clarify:
What You Have: Event-Driven Modular Monolith
This architecture is a single deployable application (monolith) with event-driven communication patterns and modular
structure.
It's a Monolith Because:

✅ Single codebase
✅ Single deployment unit (one JAR/container)
✅ Single database (PostgreSQL)
✅ In-process method calls between services
✅ Single Spring Boot application

It's Modular Because:

✅ Clear domain boundaries (Post, User, Timeline as separate packages)
✅ Services don't directly call each other across domains
✅ Could be split into microservices later without major refactoring

It's Event-Driven Because:

✅ Uses events (SNS/SQS) for cross-domain communication
✅ Asynchronous processing
✅ Loose coupling between domains
✅ Transactional outbox pattern

The Spectrum
Simple Monolith → Modular Monolith → Event-Driven Monolith (current) → Microservices

event-driven-social/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── docker-compose.yml
├── README.md
├── .gitignore
│
├── terraform/
│ ├── modules/
│ │ ├── messaging/
│ │ │ ├── main.tf
│ │ │ ├── variables.tf
│ │ │ └── outputs.tf
│ │ ├── database/
│ │ │ ├── main.tf
│ │ │ ├── variables.tf
│ │ │ └── outputs.tf
│ │ └── iam/
│ │ ├── main.tf
│ │ ├── variables.tf
│ │ └── outputs.tf
│ └── environments/
│ ├── dev/
│ │ ├── main.tf
│ │ ├── variables.tf
│ │ ├── terraform.tfvars
│ │ └── outputs.tf
│ └── prod/
│ ├── main.tf
│ ├── variables.tf
│ ├── terraform.tfvars
│ └── outputs.tf
│
├── scripts/
│ ├── setup-local-infra.sh
│ ├── apply-terraform.sh
│ └── run-tests.sh
│
└── src/
├── main/
│ ├── kotlin/
│ │ └── com/example/social/
│ │ ├── SocialPlatformApplication.kt
│ │ │
│ │ ├── config/
│ │ │ ├── AwsConfiguration.kt
│ │ │ ├── AwsProperties.kt
│ │ │ └── DatabaseConfiguration.kt
│ │ │
│ │ ├── domain/
│ │ │ ├── model/
│ │ │ │ ├── Post.kt
│ │ │ │ ├── User.kt
│ │ │ │ └── OutboxEvent.kt
│ │ │ │
│ │ │ ├── repository/
│ │ │ │ ├── PostRepository.kt
│ │ │ │ ├── UserRepository.kt
│ │ │ │ └── OutboxEventRepository.kt
│ │ │ │
│ │ │ └── service/
│ │ │ ├── PostService.kt
│ │ │ ├── UserService.kt
│ │ │ └── TimelineService.kt
│ │ │
│ │ ├── events/
│ │ │ ├── DomainEvent.kt
│ │ │ ├── PostCreatedEvent.kt
│ │ │ ├── UserFollowedEvent.kt
│ │ │ ├── PostLikedEvent.kt
│ │ │ │
│ │ │ ├── publisher/
│ │ │ │ ├── OutboxPublisher.kt
│ │ │ │ └── EventPublisher.kt
│ │ │ │
│ │ │ └── consumer/
│ │ │ ├── TimelineEventConsumer.kt
│ │ │ └── NotificationEventConsumer.kt
│ │ │
│ │ └── web/
│ │ ├── controller/
│ │ │ ├── PostController.kt
│ │ │ ├── UserController.kt
│ │ │ └── TimelineController.kt
│ │ │
│ │ ├── dto/
│ │ │ ├── request/
│ │ │ │ ├── CreatePostRequest.kt
│ │ │ │ ├── CreateUserRequest.kt
│ │ │ │ └── FollowUserRequest.kt
│ │ │ │
│ │ │ └── response/
│ │ │ ├── PostResponse.kt
│ │ │ ├── UserResponse.kt
│ │ │ └── TimelineResponse.kt
│ │ │
│ │ └── exception/
│ │ ├── GlobalExceptionHandler.kt
│ │ └── ResourceNotFoundException.kt
│ │
│ └── resources/
│ ├── application.yml
│ ├── application-dev.yml
│ ├── application-prod.yml
│ │
│ └── db/migration/
│ ├── V1__create_users_table.sql
│ ├── V2__create_posts_table.sql
│ ├── V3__create_outbox_events_table.sql
│ ├── V4__create_follows_table.sql
│ └── V5__create_indexes.sql
│
└── test/
└── kotlin/
└── com/example/social/
├── SocialPlatformApplicationTests.kt
│
├── domain/
│ ├── repository/
│ │ ├── PostRepositoryTest.kt
│ │ ├── UserRepositoryTest.kt
│ │ └── OutboxEventRepositoryTest.kt
│ │
│ └── service/
│ ├── PostServiceTest.kt
│ ├── UserServiceTest.kt
│ └── TimelineServiceTest.kt
│
├── events/
│ └── publisher/
│ └── OutboxPublisherTest.kt
│
├── web/
│ └── controller/
│ ├── PostControllerTest.kt
│ └── UserControllerTest.kt
│
└── integration/
├── PostCreationIntegrationTest.kt
└── EventPublishingIntegrationTest.kt

# Project Overview: Event-Driven Social Media Backend

## Context

This is an overview of the project structure and current state, as requested.

## Stack

- **Spring Boot 4.0.2** + **Kotlin 2.2.21** + **PostgreSQL 16**
- Spring Data JPA, Spring Validation, Flyway migrations
- AWS SDK v2: SNS (event publishing) + SQS (consumers — not yet implemented)
- Kotlin coroutines (core + reactor)
- Quartz (scheduling for outbox publisher)
- Jackson with Kotlin + JavaTime modules
- Testing: JUnit 5, Mockito + mockito-kotlin, Testcontainers (PostgreSQL)

## Architecture

```
src/main/kotlin/io/github/snorrefo/event_driven_social/
├── EventDrivenSocialApplication.kt          — Entry point
├── config/
│   ├── AwsConfiguration.kt                  — SNS/SQS clients, @EnableScheduling
│   ├── AwsProperties.kt                     — AWS region, topic ARNs, queue URLs
│   └── JacksonConfiguration.kt              — ObjectMapper with Kotlin + JSR310
├── domain/
│   ├── model/
│   │   ├── User.kt                          — JPA entity (UUID, username, displayName, bio, avatarUrl)
│   │   ├── Post.kt                          — JPA entity (UUID, authorId, content ≤280, mediaUrls, inReplyToPostId)
│   │   └── OutboxEvent.kt                   — Transactional outbox (aggregateType, eventType, payload JSON)
│   ├── repository/
│   │   ├── UserRepository.kt                — findByUsername, existsByUsername
│   │   ├── PostRepository.kt                — findByAuthorId (paginated), findTimelineForUsers, countByAuthorId
│   │   └── OutboxEventRepository.kt         — findTop100 unpublished, deletePublishedBefore
│   └── service/
│       ├── UserService.kt                   — createUser (validation), getUser, getUserByUsername
│       └── PostService.kt                   — createPost (+ outbox event), getPost, getUserPosts, getPostCount
├── events/
│   ├── DomainEvent.kt                       — Sealed base class
│   ├── PostCreatedEvent.kt                  — eventId, authorId, content, mediaUrls
│   ├── PostLikedEvent.kt                    — postId, userId, likedAt
│   ├── UserFollowedEvent.kt                 — followerId, followedUserId, followedAt
│   └── publisher/
│       └── OutboxPublisher.kt               — Polls every 5s, publishes to SNS, daily cleanup
└── web/
    ├── controller/
    │   ├── PostController.kt                — POST/GET /api/posts, GET /api/posts/user/{userId}
    │   ├── UserController.kt                — POST/GET /api/users, GET /api/users/username/{username}
    │   └── GlobalExceptionHandler.kt        — Validation error → 400 with {"errors": [...]}
    └── dto/
        ├── request/
        │   ├── CreatePostRequest.kt         — content, inReplyToPostId?, mediaUrls
        │   └── CreateUserRequest.kt         — username, displayName, bio?, avatarUrl?
        └── response/
            ├── PostResponse.kt              — + toResponse() extension
            └── UserResponse.kt              — + toResponse() extension
```

## Tests

```
src/test/kotlin/io/github/snorrefo/event_driven_social/domain/
├── TestContainersConfiguration.kt           — PostgreSQL 16 container bean
├── repository/
│   ├── PostRepositoryTest.kt                — @DataJpaTest + Testcontainers
│   └── UserRepositoryTest.kt                — @DataJpaTest + Testcontainers
├── service/
│   ├── PostServiceTest.kt                   — Unit tests with Mockito mocks
│   └── UserServiceTest.kt                   — Unit tests with Mockito mocks
└── web/controller/
    └── PostControllerTest.kt                — @SpringBootTest + MockMvc + @MockitoBean

```

## Database (Flyway migrations)

- `V1` — `users` table (UUID PK, unique username, timestamps)
- `V2` — `posts` table (author FK, 280-char limit) + `post_media` table (media URLs, CASCADE delete)
- `V3` — `outbox_events` table (transactional outbox pattern, indexes for polling)
- `V4` — `follows` table (composite PK follower_id + followed_id)

## Key Design Patterns

- **Transactional Outbox**: Post creation saves both the Post and an OutboxEvent in the same transaction.
  OutboxPublisher polls unpublished events every 5s and publishes to SNS.
- **Event types**: `post.created`, `user.followed`, `post.liked` → routed to corresponding SNS topics
- **Validation**: dual-layer — Jakarta Bean Validation on DTOs (controller level) + business rules in services

## build.gradle.kts Notes

- Java 21 toolchain
- `mockito-kotlin:5.4.0` added explicitly (not in Spring Boot BOM)
- MockK removed (commented out)
- Testcontainers BOM `1.20.4`
- Kotlin compiler flags: `-Xjsr305=strict`, `-Xannotation-default-target=param-property`

## What's Not Yet Implemented

- SQS consumers (timeline/notification event processing)
- Follow service (V4 migration exists but no FollowService/FollowRepository/FollowController)
- Like functionality (PostLikedEvent exists but no LikeService)
- Timeline aggregation (PostRepository.findTimelineForUsers exists but no TimelineService)
- LocalStack config in docker-compose.yml (commented out)
- UserControllerTest (no test file exists)
