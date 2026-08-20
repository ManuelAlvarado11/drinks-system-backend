-- ============================================================================
-- V3__sales_create_tables.sql
-- All tables for the SALES schema: customers, cash_registers, cash_register_movements,
-- accounts, account_details, sales, sale_details
-- ============================================================================

SET search_path TO sales;

-- =============================================================================
-- Tabla: customers
-- =============================================================================
CREATE TABLE sales.customers (
    id              BIGSERIAL PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100),
    nit_ci          VARCHAR(30),
    phone           VARCHAR(20),
    email           VARCHAR(150),
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT
);

CREATE INDEX idx_customers_is_active ON sales.customers(is_active) WHERE is_active = true;
CREATE INDEX idx_customers_nit_ci ON sales.customers(nit_ci);

-- =============================================================================
-- Tabla: cash_registers (cajas registradoras / turnos de caja)
-- =============================================================================
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

-- =============================================================================
-- Tabla: cash_register_movements
-- =============================================================================
CREATE TABLE sales.cash_register_movements (
    id                  BIGSERIAL PRIMARY KEY,
    cash_register_id    BIGINT NOT NULL REFERENCES sales.cash_registers(id) ON DELETE RESTRICT,
    movement_type       VARCHAR(30) NOT NULL CHECK (movement_type IN ('DEPOSIT', 'WITHDRAWAL', 'SALE_INCOME')),
    amount              NUMERIC(12,2) NOT NULL,
    description         VARCHAR(300),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          BIGINT
);

CREATE INDEX idx_cash_reg_movements_register_id ON sales.cash_register_movements(cash_register_id);

-- =============================================================================
-- Tabla: accounts (cuentas abiertas / mesas)
-- =============================================================================
CREATE TABLE sales.accounts (
    id              BIGSERIAL PRIMARY KEY,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    customer_name   VARCHAR(100),
    customer_last_name VARCHAR(100),
    table_number    VARCHAR(20),
    internal_code   VARCHAR(50),
    status          VARCHAR(30) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'CLOSED', 'CANCELLED')),
    opened_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    closed_at       TIMESTAMPTZ,
    opened_by       BIGINT NOT NULL,
    closed_by       BIGINT,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_accounts_branch_id ON sales.accounts(branch_id);
CREATE INDEX idx_accounts_status ON sales.accounts(status);
CREATE INDEX idx_accounts_opened_at ON sales.accounts(opened_at);

-- =============================================================================
-- Tabla: account_details (items de una cuenta)
-- =============================================================================
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

-- =============================================================================
-- Tabla: sales (ventas facturadas)
-- =============================================================================
CREATE TABLE sales.sales (
    id              BIGSERIAL PRIMARY KEY,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    account_id      BIGINT REFERENCES sales.accounts(id) ON DELETE SET NULL,
    customer_id     BIGINT REFERENCES sales.customers(id) ON DELETE SET NULL,
    cash_register_id BIGINT REFERENCES sales.cash_registers(id) ON DELETE RESTRICT,
    sale_number     VARCHAR(30) NOT NULL,
    subtotal        NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(12,2) NOT NULL DEFAULT 0,
    payment_method  VARCHAR(30) NOT NULL DEFAULT 'CASH',
    status          VARCHAR(30) NOT NULL DEFAULT 'COMPLETED' CHECK (status IN ('COMPLETED', 'CANCELLED', 'PENDING')),
    sale_date       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT NOT NULL,
    updated_by      BIGINT
);

CREATE INDEX idx_sales_branch_id ON sales.sales(branch_id);
CREATE INDEX idx_sales_customer_id ON sales.sales(customer_id);
CREATE INDEX idx_sales_cash_register_id ON sales.sales(cash_register_id);
CREATE INDEX idx_sales_account_id ON sales.sales(account_id);
CREATE INDEX idx_sales_sale_date ON sales.sales(sale_date);
CREATE INDEX idx_sales_branch_date ON sales.sales(branch_id, sale_date);
CREATE INDEX idx_sales_status ON sales.sales(status);
CREATE INDEX idx_sales_sale_number ON sales.sales(sale_number);
CREATE UNIQUE INDEX idx_sales_branch_sale_number ON sales.sales(branch_id, sale_number);

-- =============================================================================
-- Tabla: sale_details
-- =============================================================================
CREATE TABLE sales.sale_details (
    id              BIGSERIAL PRIMARY KEY,
    sale_id         BIGINT NOT NULL REFERENCES sales.sales(id) ON DELETE CASCADE,
    product_id      BIGINT NOT NULL REFERENCES inventory.products(id) ON DELETE RESTRICT,
    quantity        INTEGER NOT NULL CHECK (quantity > 0),
    unit_price      NUMERIC(12,2) NOT NULL,
    subtotal        NUMERIC(12,2) NOT NULL,
    discount        NUMERIC(12,2) NOT NULL DEFAULT 0
);

CREATE INDEX idx_sale_details_sale_id ON sales.sale_details(sale_id);
CREATE INDEX idx_sale_details_product_id ON sales.sale_details(product_id);
