SET search_path TO inventory;

CREATE TABLE inventory.products (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(150) NOT NULL,
    category_id     BIGINT REFERENCES inventory.categories(id) ON DELETE SET NULL,
    size            VARCHAR(50),
    description     TEXT,
    cost_price      NUMERIC(12,2) NOT NULL DEFAULT 0,
    sale_price      NUMERIC(12,2) NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT
);

CREATE INDEX idx_products_category_id ON inventory.products(category_id);
CREATE INDEX idx_products_code ON inventory.products(code);
CREATE INDEX idx_products_is_active ON inventory.products(is_active) WHERE is_active = true;
CREATE INDEX idx_products_name ON inventory.products(name);
