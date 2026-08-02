SET search_path TO reporting;

CREATE TABLE reporting.monthly_sales_summary (
    id                  BIGSERIAL PRIMARY KEY,
    branch_id           BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    year                INTEGER NOT NULL,
    month               INTEGER NOT NULL CHECK (month BETWEEN 1 AND 12),
    total_sales_count   INTEGER NOT NULL DEFAULT 0,
    total_revenue       NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_discount      NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_tax           NUMERIC(14,2) NOT NULL DEFAULT 0,
    net_revenue         NUMERIC(14,2) NOT NULL DEFAULT 0,
    refreshed_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(branch_id, year, month)
);

CREATE INDEX idx_monthly_summary_branch ON reporting.monthly_sales_summary(branch_id);
CREATE INDEX idx_monthly_summary_period ON reporting.monthly_sales_summary(year, month);
