SET search_path TO sales;

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
