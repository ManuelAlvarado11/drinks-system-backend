SET search_path TO sales;

CREATE TABLE sales.accounts (
    id                  BIGSERIAL PRIMARY KEY,
    branch_id           BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    customer_name       VARCHAR(100),
    customer_last_name  VARCHAR(100),
    table_number        VARCHAR(20),
    internal_code       VARCHAR(50),
    status              VARCHAR(30) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'CLOSED', 'CANCELLED')),
    opened_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_at           TIMESTAMPTZ,
    opened_by           BIGINT NOT NULL,
    closed_by           BIGINT,
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_accounts_branch_id ON sales.accounts(branch_id);
CREATE INDEX idx_accounts_status ON sales.accounts(status);
CREATE INDEX idx_accounts_branch_status ON sales.accounts(branch_id, status);
CREATE INDEX idx_accounts_opened_at ON sales.accounts(opened_at);
CREATE INDEX idx_accounts_opened_by ON sales.accounts(opened_by);

CREATE TABLE sales.account_details (
    id              BIGSERIAL PRIMARY KEY,
    account_id      BIGINT NOT NULL REFERENCES sales.accounts(id) ON DELETE RESTRICT,
    product_id      BIGINT NOT NULL REFERENCES inventory.products(id) ON DELETE RESTRICT,
    quantity        INTEGER NOT NULL CHECK (quantity > 0),
    unit_price      NUMERIC(12,2) NOT NULL,
    subtotal        NUMERIC(12,2) NOT NULL,
    added_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    added_by        BIGINT NOT NULL,
    is_cancelled    BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_account_details_account_id ON sales.account_details(account_id);
CREATE INDEX idx_account_details_product_id ON sales.account_details(product_id);
CREATE INDEX idx_account_details_added_at ON sales.account_details(added_at);
