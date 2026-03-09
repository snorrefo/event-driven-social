CREATE TABLE users
(
    id           UUID PRIMARY KEY,
    username     VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100)       NOT NULL,
    bio          VARCHAR(160),
    avatar_url   VARCHAR(500),
    created_at   TIMESTAMP          NOT NULL
);

CREATE INDEX idx_users_username ON users (username);