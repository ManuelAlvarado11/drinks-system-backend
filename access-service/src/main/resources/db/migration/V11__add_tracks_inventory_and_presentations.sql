-- ============================================================================
-- V11__add_tracks_inventory_and_presentations.sql
-- 1. Adds tracks_inventory flag to products (default true for existing products)
-- 2. Creates product_presentations table for volume-based pricing (balde, hielera)
-- ============================================================================

SET search_path TO inventory;

-- =============================================================================
-- 1. Flag para controlar si un producto lleva inventario
--    true  = Bebidas embotelladas (stock se deduce al vender)
--    false = Comida, shots, tragos preparados (solo venta, sin stock)
-- =============================================================================
ALTER TABLE inventory.products
    ADD COLUMN tracks_inventory BOOLEAN NOT NULL DEFAULT true;

-- =============================================================================
-- 2. Presentaciones de producto (precios por volumen)
--    Ejemplo: Pilsener → Balde (6 unidades, $7.50), Hielera (24 unidades, $30.00)
--    El POS calcula: unit_price = price / quantity al agregar a la cuenta
-- =============================================================================
CREATE TABLE inventory.product_presentations (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES inventory.products(id) ON DELETE CASCADE,
    name        VARCHAR(50) NOT NULL,
    quantity    INTEGER NOT NULL CHECK (quantity > 1),
    price       NUMERIC(12,2) NOT NULL CHECK (price > 0),
    is_active   BOOLEAN NOT NULL DEFAULT true,
    sort_order  INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_product_presentations_product_id ON inventory.product_presentations(product_id);
CREATE INDEX idx_product_presentations_active ON inventory.product_presentations(is_active) WHERE is_active = true;

-- =============================================================================
-- 3. Grants para el service user
-- =============================================================================
GRANT ALL ON inventory.product_presentations TO inventory_user;
GRANT ALL ON inventory.product_presentations_id_seq TO inventory_user;
