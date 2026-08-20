-- ============================================================================
-- V4__reporting_create_tables.sql
-- All tables for the REPORTING schema: daily_sales_summary,
-- monthly_sales_summary, product_sales_ranking, inventory_status_view
-- ============================================================================

SET search_path TO reporting;

-- =============================================================================
-- Tabla: daily_sales_summary
-- =============================================================================
CREATE TABLE reporting.daily_sales_summary (
    id                  BIGSERIAL PRIMARY KEY,
    branch_id           BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    summary_date        DATE NOT NULL,
    total_sales_count   INTEGER NOT NULL DEFAULT 0,
    total_revenue       NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_discount      NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_tax           NUMERIC(12,2) NOT NULL DEFAULT 0,
    net_revenue         NUMERIC(12,2) NOT NULL DEFAULT 0,
    refreshed_at        TIMESTAMPTZ,
    UNIQUE(branch_id, summary_date)
);

CREATE INDEX idx_daily_summary_branch_date ON reporting.daily_sales_summary(branch_id, summary_date);

-- =============================================================================
-- Tabla: monthly_sales_summary
-- =============================================================================
CREATE TABLE reporting.monthly_sales_summary (
    id                  BIGSERIAL PRIMARY KEY,
    branch_id           BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    year                INTEGER NOT NULL,
    month               INTEGER NOT NULL CHECK (month BETWEEN 1 AND 12),
    total_sales_count   INTEGER NOT NULL DEFAULT 0,
    total_revenue       NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_discount      NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_tax           NUMERIC(12,2) NOT NULL DEFAULT 0,
    net_revenue         NUMERIC(12,2) NOT NULL DEFAULT 0,
    refreshed_at        TIMESTAMPTZ,
    UNIQUE(branch_id, year, month)
);

CREATE INDEX idx_monthly_summary_branch_year ON reporting.monthly_sales_summary(branch_id, year);

-- =============================================================================
-- Tabla: product_sales_ranking
-- =============================================================================
CREATE TABLE reporting.product_sales_ranking (
    id                      BIGSERIAL PRIMARY KEY,
    product_id              BIGINT REFERENCES inventory.products(id) ON DELETE SET NULL,
    branch_id               BIGINT REFERENCES access.branches(id) ON DELETE SET NULL,
    product_name            VARCHAR(150),
    category_name           VARCHAR(100),
    total_quantity_sold     INTEGER NOT NULL DEFAULT 0,
    total_revenue           NUMERIC(12,2) NOT NULL DEFAULT 0,
    profit                  NUMERIC(12,2) NOT NULL DEFAULT 0,
    period_start            DATE,
    period_end              DATE,
    refreshed_at            TIMESTAMPTZ
);

CREATE INDEX idx_product_ranking_branch ON reporting.product_sales_ranking(branch_id);
CREATE INDEX idx_product_ranking_product ON reporting.product_sales_ranking(product_id);

-- =============================================================================
-- Tabla: inventory_status_view
-- =============================================================================
CREATE TABLE reporting.inventory_status_view (
    id                  BIGSERIAL PRIMARY KEY,
    product_id          BIGINT REFERENCES inventory.products(id) ON DELETE SET NULL,
    branch_id           BIGINT REFERENCES access.branches(id) ON DELETE SET NULL,
    product_name        VARCHAR(150),
    category_name       VARCHAR(100),
    current_stock       INTEGER NOT NULL DEFAULT 0,
    minimum_stock       INTEGER NOT NULL DEFAULT 0,
    cost_price          NUMERIC(12,2) NOT NULL DEFAULT 0,
    sale_price          NUMERIC(12,2) NOT NULL DEFAULT 0,
    is_low_stock        BOOLEAN NOT NULL DEFAULT false,
    refreshed_at        TIMESTAMPTZ
);

CREATE INDEX idx_inv_status_branch ON reporting.inventory_status_view(branch_id);
CREATE INDEX idx_inv_status_low_stock ON reporting.inventory_status_view(is_low_stock) WHERE is_low_stock = true;
