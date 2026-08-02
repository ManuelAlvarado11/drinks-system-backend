SET search_path TO inventory;

CREATE TABLE inventory.product_stock (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT NOT NULL REFERENCES inventory.products(id) ON DELETE CASCADE,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    current_stock   INTEGER NOT NULL DEFAULT 0,
    minimum_stock   INTEGER NOT NULL DEFAULT 0,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(product_id, branch_id)
);

CREATE INDEX idx_product_stock_product_id ON inventory.product_stock(product_id);
CREATE INDEX idx_product_stock_branch_id ON inventory.product_stock(branch_id);
CREATE INDEX idx_product_stock_low ON inventory.product_stock(branch_id) WHERE current_stock <= minimum_stock;
