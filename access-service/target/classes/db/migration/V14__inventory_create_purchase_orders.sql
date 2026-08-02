SET search_path TO inventory;

CREATE TABLE inventory.purchase_orders (
    id              BIGSERIAL PRIMARY KEY,
    supplier_id     BIGINT NOT NULL REFERENCES inventory.suppliers(id) ON DELETE RESTRICT,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    order_number    VARCHAR(30) NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'RECEIVED', 'PARTIAL', 'CANCELLED')),
    total_amount    NUMERIC(12,2) NOT NULL DEFAULT 0,
    order_date      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    received_date   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT NOT NULL,
    updated_by      BIGINT
);

CREATE INDEX idx_purchase_orders_supplier_id ON inventory.purchase_orders(supplier_id);
CREATE INDEX idx_purchase_orders_branch_id ON inventory.purchase_orders(branch_id);
CREATE INDEX idx_purchase_orders_status ON inventory.purchase_orders(status);
CREATE INDEX idx_purchase_orders_order_date ON inventory.purchase_orders(order_date);

CREATE TABLE inventory.purchase_order_details (
    id                  BIGSERIAL PRIMARY KEY,
    purchase_order_id   BIGINT NOT NULL REFERENCES inventory.purchase_orders(id) ON DELETE CASCADE,
    product_id          BIGINT NOT NULL REFERENCES inventory.products(id) ON DELETE RESTRICT,
    quantity_ordered    INTEGER NOT NULL CHECK (quantity_ordered > 0),
    quantity_received   INTEGER NOT NULL DEFAULT 0 CHECK (quantity_received >= 0),
    unit_cost           NUMERIC(12,2) NOT NULL,
    subtotal            NUMERIC(12,2) NOT NULL
);

CREATE INDEX idx_po_details_order_id ON inventory.purchase_order_details(purchase_order_id);
CREATE INDEX idx_po_details_product_id ON inventory.purchase_order_details(product_id);
