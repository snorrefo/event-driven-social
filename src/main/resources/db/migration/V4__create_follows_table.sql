CREATE TABLE follows
(
    follower_id UUID      NOT NULL,
    followed_id UUID      NOT NULL,
    followed_at TIMESTAMP NOT NULL,

    PRIMARY KEY (follower_id, followed_id)
);

CREATE INDEX idx_follows_follower ON follows (follower_id);
CREATE INDEX idx_follows_followed ON follows (followed_id);
