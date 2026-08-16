-- V36__grant_service_user_permissions.sql
-- Grants full permissions on all existing tables and sequences to service users.
-- This is needed because DEFAULT PRIVILEGES only apply to future objects,
-- not to tables/sequences created by Flyway (which runs as drinks_admin).

-- Access schema
GRANT ALL ON ALL TABLES IN SCHEMA access TO access_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA access TO access_user;

-- Sales schema
GRANT ALL ON ALL TABLES IN SCHEMA sales TO sales_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA sales TO sales_user;

-- Inventory schema
GRANT ALL ON ALL TABLES IN SCHEMA inventory TO inventory_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA inventory TO inventory_user;

-- Reporting schema
GRANT ALL ON ALL TABLES IN SCHEMA reporting TO reporting_user;
GRANT ALL ON ALL SEQUENCES IN SCHEMA reporting TO reporting_user;

-- Reporting user needs read access to sales and inventory for scheduled jobs
GRANT SELECT ON ALL TABLES IN SCHEMA sales TO reporting_user;
GRANT SELECT ON ALL TABLES IN SCHEMA inventory TO reporting_user;
