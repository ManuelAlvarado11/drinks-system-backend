-- ============================================================================
-- V14__add_branch_printers.sql
-- Configurable ESC/POS receipt printers per branch (scalable to N printers).
-- The backend prints tickets by opening a raw TCP socket to ip:port (RAW/9100).
-- ============================================================================

SET search_path TO sales;

CREATE TABLE sales.branch_printers (
    id              BIGSERIAL PRIMARY KEY,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    -- Connection: raw TCP (ESC/POS over port 9100 / JetDirect)
    host            VARCHAR(100) NOT NULL,
    port            INTEGER NOT NULL DEFAULT 9100 CHECK (port > 0 AND port <= 65535),
    -- Paper width in mm (80mm => 48 chars, 58mm => 32 chars)
    paper_width_mm  INTEGER NOT NULL DEFAULT 80 CHECK (paper_width_mm IN (58, 80)),
    -- Number of copies to print per ticket
    copies          INTEGER NOT NULL DEFAULT 1 CHECK (copies >= 1 AND copies <= 5),
    -- Whether to send an ESC/POS cut command at the end
    cut_paper       BOOLEAN NOT NULL DEFAULT true,
    -- Whether to open the cash drawer (kick) after printing
    open_drawer     BOOLEAN NOT NULL DEFAULT false,
    -- Exactly one printer per branch should be the default target
    is_default      BOOLEAN NOT NULL DEFAULT false,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT
);

CREATE INDEX idx_branch_printers_branch_id ON sales.branch_printers(branch_id);

-- Only one default printer per branch (partial unique index)
CREATE UNIQUE INDEX uq_branch_printers_default
    ON sales.branch_printers(branch_id)
    WHERE is_default = true;

-- Grant privileges to the sales service role (see V10 pattern)
GRANT ALL ON sales.branch_printers TO sales_user;
GRANT ALL ON SEQUENCE sales.branch_printers_id_seq TO sales_user;
