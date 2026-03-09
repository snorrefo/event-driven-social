CREATE TABLE posts
(
    id UUID PRIMARY KEY,
    author_id UUID NOT NULL,
    content VARCHAR(280) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    in_reply_to_post_id UUID,

    FOREIGN KEY (in_reply_to_post_id) REFERENCES posts(id)
);

CREATE TABLE post_media
(
    post_id UUID NOT NULL,
    media_url VARCHAR(500) NOT NULL,

    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE INDEX idx_posts_author_id ON posts(author_id);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_posts_reply_to ON posts(in_reply_to_post_id) WHERE in_reply_to_post_id IS NOT NULL;
