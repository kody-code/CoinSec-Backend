CREATE TABLE users (
    user_id    BIGSERIAL    PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    password   VARCHAR(100) NOT NULL,
    nickname   VARCHAR(50),
    avatar     VARCHAR(255),
    create_time TIMESTAMP,
    is_deleted  BOOLEAN     DEFAULT FALSE
);

CREATE TABLE accounts (
    account_id BIGSERIAL       PRIMARY KEY,
    user_id    BIGINT          NOT NULL,
    name       VARCHAR(30)     NOT NULL,
    icon       VARCHAR(255),
    balance    NUMERIC(12, 2)  DEFAULT 0,
    status     INTEGER         DEFAULT 1,
    is_deleted BOOLEAN         DEFAULT FALSE
);

CREATE TABLE categories (
    category_id BIGSERIAL      PRIMARY KEY,
    user_id     BIGINT         NOT NULL,
    name        VARCHAR(30)    NOT NULL,
    type        VARCHAR(10)    NOT NULL,
    icon        VARCHAR(255),
    sort        INTEGER        DEFAULT 0,
    is_deleted  BOOLEAN        DEFAULT FALSE
);

CREATE TABLE records (
    record_id   BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL,
    category_id BIGINT          NOT NULL,
    account_id  BIGINT          NOT NULL,
    type        VARCHAR(10)     NOT NULL,
    amount      NUMERIC(12, 2)  NOT NULL,
    remark      VARCHAR(255),
    record_time TIMESTAMP       NOT NULL,
    is_deleted  BOOLEAN         DEFAULT FALSE
);

CREATE TABLE transfers (
    transfer_id    BIGSERIAL       PRIMARY KEY,
    user_id        BIGINT          NOT NULL,
    from_account_id BIGINT         NOT NULL,
    to_account_id   BIGINT         NOT NULL,
    amount         NUMERIC(12, 2)  NOT NULL,
    transfer_time  TIMESTAMP       NOT NULL,
    remark         VARCHAR(255),
    is_deleted     BOOLEAN         DEFAULT FALSE
);
