SET search_path TO reporting;

CREATE TABLE reporting.inventory_status_view (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT NOT NULL REFERENCES inventory.products(id) ON DELETE RESTRICT,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    product_name    VARCHAR(150) NOT NULL,
    category_name   VARCHAR(100),
    current_stock   INTEGER NOT NULL DEFAULT 0,
    minimum_stock   INTEGER NOT NULL DEFAULT 0,
    cost_price      NUMERIC(12,2) NOT NULL DEFAULT 0,
    sale_price      NUMERIC(12,2) NOT NULL DEFAULT 0,
    is_low_stock    BOOLEAN NOT NULL DEFAULT false,
    refreshed_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inv_status_branch ON reporting.inventory_status_view(branch_id);
CREATE INDEX idx_inv_status_product ON reporting.inventory_status_view(product_id);
CREATE INDEX idx_inv_status_low_stock ON reporting.inventory_status_view(is_low_stock) WHERE is_low_stock = true;
