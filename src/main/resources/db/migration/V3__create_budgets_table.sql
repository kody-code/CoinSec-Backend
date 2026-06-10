CREATE TABLE budgets (
    budget_id     BIGSERIAL       PRIMARY KEY,
    user_id       BIGINT          NOT NULL,
    category_id   BIGINT          NOT NULL,
    budget_amount NUMERIC(12, 2)  NOT NULL,
    period_type   VARCHAR(20)     NOT NULL,
    period_year   INTEGER         NOT NULL,
    period_month  INTEGER,
    is_deleted    BOOLEAN         DEFAULT FALSE
);
