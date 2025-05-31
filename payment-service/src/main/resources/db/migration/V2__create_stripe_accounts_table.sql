CREATE TABLE stripe_accounts (
                                 id UUID PRIMARY KEY,
                                 user_id UUID NOT NULL UNIQUE,
                                 stripe_account_id VARCHAR(255) NOT NULL,
                                 created_at TIMESTAMPTZ NOT NULL
);
