CREATE TABLE record_tags (
    record_id BIGINT NOT NULL REFERENCES records(record_id),
    tag_id    BIGINT NOT NULL REFERENCES tags(tag_id),
    PRIMARY KEY (record_id, tag_id)
);
