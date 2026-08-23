-- ============================================================================
-- V10__grant_service_permissions.sql
-- PostgreSQL role grants for each microservice user.
-- This is needed because DEFAULT PRIVILEGES only apply to future objects,
-- not to tables/sequences created by Flyway (which runs as drinks_admin).
-- ============================================================================

-- =============================================================================
-- Access schema → access_user
-- =============================================================================
GRANT ALL ON ALL TABLES IN SCHEMA access TO access_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA access TO access_user;

-- =============================================================================
-- Sales schema → sales_user
-- =============================================================================
GRANT ALL ON ALL TABLES IN SCHEMA sales TO sales_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA sales TO sales_user;

-- =============================================================================
-- Inventory schema → inventory_user
-- =============================================================================
GRANT ALL ON ALL TABLES IN SCHEMA inventory TO inventory_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA inventory TO inventory_user;

-- =============================================================================
-- Reporting schema → reporting_user
-- =============================================================================
GRANT ALL ON ALL TABLES IN SCHEMA reporting TO reporting_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA reporting TO reporting_user;

-- =============================================================================
-- Reporting user needs cross-schema read access for scheduled refresh jobs
-- =============================================================================
GRANT USAGE ON SCHEMA sales TO reporting_user;
GRANT SELECT ON ALL TABLES IN SCHEMA sales TO reporting_user;

GRANT USAGE ON SCHEMA inventory TO reporting_user;
GRANT SELECT ON ALL TABLES IN SCHEMA inventory TO reporting_user;

-- =============================================================================
-- Sales user needs cross-schema read access for name resolution
-- (product names from inventory, user names from access)
-- =============================================================================
GRANT USAGE ON SCHEMA access TO sales_user;
GRANT SELECT ON ALL TABLES IN SCHEMA access TO sales_user;

GRANT USAGE ON SCHEMA inventory TO sales_user;
GRANT SELECT ON ALL TABLES IN SCHEMA inventory TO sales_user;

-- =============================================================================
-- Inventory user needs cross-schema read access for branch name resolution
-- =============================================================================
GRANT USAGE ON SCHEMA access TO inventory_user;
GRANT SELECT ON ALL TABLES IN SCHEMA access TO inventory_user;
