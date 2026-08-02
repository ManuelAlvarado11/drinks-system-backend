SET search_path TO reporting;

CREATE TABLE reporting.daily_sales_summary (
    id                  BIGSERIAL PRIMARY KEY,
    branch_id           BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE RESTRICT,
    summary_date        DATE NOT NULL,
    total_sales_count   INTEGER NOT NULL DEFAULT 0,
    total_revenue       NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_discount      NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_tax           NUMERIC(14,2) NOT NULL DEFAULT 0,
    net_revenue         NUMERIC(14,2) NOT NULL DEFAULT 0,
    refreshed_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(branch_id, summary_date)
);

CREATE INDEX idx_daily_summary_branch ON reporting.daily_sales_summary(branch_id);
CREATE INDEX idx_daily_summary_date ON reporting.daily_sales_summary(summary_date);
CREATE INDEX idx_daily_summary_branch_date ON reporting.daily_sales_summary(branch_id, summary_date);
