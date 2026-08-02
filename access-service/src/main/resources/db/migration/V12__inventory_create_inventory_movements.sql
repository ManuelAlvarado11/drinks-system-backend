SET search_path TO inventory;

CREATE TABLE inventory.inventory_movements (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT NOT NULL REFERENCES inventory.products(id) ON DELETE RESTRICT,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    movement_type   VARCHAR(30) NOT NULL CHECK (movement_type IN ('ENTRY', 'EXIT', 'ADJUSTMENT', 'SALE', 'PURCHASE', 'TRANSFER')),
    quantity        INTEGER NOT NULL,
    previous_stock  INTEGER NOT NULL,
    new_stock       INTEGER NOT NULL,
    reference_type  VARCHAR(50),
    reference_id    BIGINT,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT NOT NULL
);

CREATE INDEX idx_inv_movements_product_id ON inventory.inventory_movements(product_id);
CREATE INDEX idx_inv_movements_branch_id ON inventory.inventory_movements(branch_id);
CREATE INDEX idx_inv_movements_type ON inventory.inventory_movements(movement_type);
CREATE INDEX idx_inv_movements_product_branch ON inventory.inventory_movements(product_id, branch_id);
CREATE INDEX idx_inv_movements_created_at ON inventory.inventory_movements(created_at);
CREATE INDEX idx_inv_movements_reference ON inventory.inventory_movements(reference_type, reference_id);
