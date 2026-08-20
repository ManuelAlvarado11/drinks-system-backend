-- ============================================================================
-- V2__inventory_create_tables.sql
-- All tables for the INVENTORY schema: categories, products, product_stock,
-- inventory_movements, suppliers, purchase_orders + details
-- ============================================================================

SET search_path TO inventory;

-- =============================================================================
-- Tabla: categories (jerárquica)
-- =============================================================================
CREATE TABLE inventory.categories (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    description         VARCHAR(300),
    parent_category_id  BIGINT REFERENCES inventory.categories(id) ON DELETE SET NULL,
    is_active           BOOLEAN NOT NULL DEFAULT true,
    deleted_at          TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT
);

CREATE INDEX idx_categories_parent ON inventory.categories(parent_category_id);
CREATE INDEX idx_categories_is_active ON inventory.categories(is_active) WHERE is_active = true;

-- =============================================================================
-- Tabla: products
-- =============================================================================
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

-- =============================================================================
-- Tabla: product_stock (stock por producto y sucursal)
-- =============================================================================
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

-- =============================================================================
-- Tabla: inventory_movements
-- =============================================================================
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
CREATE INDEX idx_inv_movements_created_at ON inventory.inventory_movements(created_at);

-- =============================================================================
-- Tabla: suppliers
-- =============================================================================
CREATE TABLE inventory.suppliers (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    contact_name    VARCHAR(150),
    phone           VARCHAR(20),
    email           VARCHAR(150),
    address         VARCHAR(300),
    nit             VARCHAR(30),
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT
);

CREATE INDEX idx_suppliers_is_active ON inventory.suppliers(is_active) WHERE is_active = true;

-- =============================================================================
-- Tabla: purchase_orders
-- =============================================================================
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

-- =============================================================================
-- Tabla: purchase_order_details
-- =============================================================================
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
