-- docker/init-schemas.sql
-- Se ejecuta automáticamente al crear el contenedor por primera vez

-- Crear esquemas
CREATE SCHEMA IF NOT EXISTS access;
CREATE SCHEMA IF NOT EXISTS sales;
CREATE SCHEMA IF NOT EXISTS inventory;
CREATE SCHEMA IF NOT EXISTS reporting;

-- Crear usuarios por servicio con permisos limitados
CREATE USER access_user WITH PASSWORD 'dev_password_access';
CREATE USER sales_user WITH PASSWORD 'dev_password_sales';
CREATE USER inventory_user WITH PASSWORD 'dev_password_inventory';
CREATE USER reporting_user WITH PASSWORD 'dev_password_reporting';

-- Permisos para access_user
GRANT USAGE ON SCHEMA access TO access_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA access TO access_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA access TO access_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA access GRANT ALL ON TABLES TO access_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA access GRANT ALL ON SEQUENCES TO access_user;

-- Permisos para sales_user (su esquema + lectura de access e inventory)
GRANT USAGE ON SCHEMA sales TO sales_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA sales TO sales_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA sales TO sales_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA sales GRANT ALL ON TABLES TO sales_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA sales GRANT ALL ON SEQUENCES TO sales_user;
GRANT USAGE ON SCHEMA access TO sales_user;
GRANT SELECT ON ALL TABLES IN SCHEMA access TO sales_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA access GRANT SELECT ON TABLES TO sales_user;
GRANT USAGE ON SCHEMA inventory TO sales_user;
GRANT SELECT ON ALL TABLES IN SCHEMA inventory TO sales_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA inventory GRANT SELECT ON TABLES TO sales_user;

-- Permisos para inventory_user (su esquema + lectura de access)
GRANT USAGE ON SCHEMA inventory TO inventory_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA inventory TO inventory_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA inventory TO inventory_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA inventory GRANT ALL ON TABLES TO inventory_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA inventory GRANT ALL ON SEQUENCES TO inventory_user;
GRANT USAGE ON SCHEMA access TO inventory_user;
GRANT SELECT ON ALL TABLES IN SCHEMA access TO inventory_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA access GRANT SELECT ON TABLES TO inventory_user;

-- Permisos para reporting_user (su esquema + lectura de todos)
GRANT USAGE ON SCHEMA reporting TO reporting_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA reporting TO reporting_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA reporting TO reporting_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA reporting GRANT ALL ON TABLES TO reporting_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA reporting GRANT ALL ON SEQUENCES TO reporting_user;
GRANT USAGE ON SCHEMA access TO reporting_user;
GRANT SELECT ON ALL TABLES IN SCHEMA access TO reporting_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA access GRANT SELECT ON TABLES TO reporting_user;
GRANT USAGE ON SCHEMA sales TO reporting_user;
GRANT SELECT ON ALL TABLES IN SCHEMA sales TO reporting_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA sales GRANT SELECT ON TABLES TO reporting_user;
GRANT USAGE ON SCHEMA inventory TO reporting_user;
GRANT SELECT ON ALL TABLES IN SCHEMA inventory TO reporting_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA inventory GRANT SELECT ON TABLES TO reporting_user;
