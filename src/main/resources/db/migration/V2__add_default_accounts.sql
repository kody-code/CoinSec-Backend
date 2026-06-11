ALTER TABLE users ADD COLUMN IF NOT EXISTS default_income_account_id BIGINT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS default_expense_account_id BIGINT;
