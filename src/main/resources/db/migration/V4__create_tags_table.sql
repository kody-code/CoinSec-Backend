CREATE TABLE tags (
    tag_id     BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    name       VARCHAR(30)  NOT NULL,
    color      VARCHAR(20),
    is_deleted BOOLEAN      DEFAULT FALSE
);
