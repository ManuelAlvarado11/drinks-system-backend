SET search_path TO sales;

CREATE TABLE sales.cash_registers (
    id              BIGSERIAL PRIMARY KEY,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    user_id         BIGINT NOT NULL,
    opening_amount  NUMERIC(12,2) NOT NULL DEFAULT 0,
    closing_amount  NUMERIC(12,2),
    expected_amount NUMERIC(12,2),
    difference      NUMERIC(12,2),
    status          VARCHAR(30) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'CLOSED')),
    opened_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_at       TIMESTAMPTZ,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT
);

CREATE INDEX idx_cash_registers_branch_id ON sales.cash_registers(branch_id);
CREATE INDEX idx_cash_registers_user_id ON sales.cash_registers(user_id);
CREATE INDEX idx_cash_registers_status ON sales.cash_registers(status);
CREATE INDEX idx_cash_registers_branch_status ON sales.cash_registers(branch_id, status);
CREATE INDEX idx_cash_registers_opened_at ON sales.cash_registers(opened_at);

CREATE TABLE sales.cash_register_movements (
    id                  BIGSERIAL PRIMARY KEY,
    cash_register_id    BIGINT NOT NULL REFERENCES sales.cash_registers(id) ON DELETE RESTRICT,
    movement_type       VARCHAR(30) NOT NULL CHECK (movement_type IN ('DEPOSIT', 'WITHDRAWAL', 'SALE_INCOME')),
    amount              NUMERIC(12,2) NOT NULL,
    description         VARCHAR(300),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          BIGINT
);

CREATE INDEX idx_cash_reg_mov_register_id ON sales.cash_register_movements(cash_register_id);
CREATE INDEX idx_cash_reg_mov_type ON sales.cash_register_movements(movement_type);
CREATE INDEX idx_cash_reg_mov_created_at ON sales.cash_register_movements(created_at);
