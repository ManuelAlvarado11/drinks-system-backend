SET search_path TO reporting;

CREATE TABLE reporting.product_sales_ranking (
    id                      BIGSERIAL PRIMARY KEY,
    product_id              BIGINT NOT NULL REFERENCES inventory.products(id) ON DELETE RESTRICT,
    branch_id               BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    product_name            VARCHAR(150) NOT NULL,
    category_name           VARCHAR(100),
    total_quantity_sold     INTEGER NOT NULL DEFAULT 0,
    total_revenue           NUMERIC(14,2) NOT NULL DEFAULT 0,
    profit                  NUMERIC(14,2) NOT NULL DEFAULT 0,
    period_start            DATE NOT NULL,
    period_end              DATE NOT NULL,
    refreshed_at            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_product_ranking_branch ON reporting.product_sales_ranking(branch_id);
CREATE INDEX idx_product_ranking_product ON reporting.product_sales_ranking(product_id);
CREATE INDEX idx_product_ranking_period ON reporting.product_sales_ranking(period_start, period_end);
CREATE INDEX idx_product_ranking_revenue ON reporting.product_sales_ranking(total_revenue DESC);
