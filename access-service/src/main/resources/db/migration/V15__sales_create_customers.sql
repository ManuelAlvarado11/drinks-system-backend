SET search_path TO sales;

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

CREATE INDEX idx_customers_nit_ci ON sales.customers(nit_ci);
CREATE INDEX idx_customers_name ON sales.customers(first_name, last_name);
CREATE INDEX idx_customers_is_active ON sales.customers(is_active) WHERE is_active = true;
