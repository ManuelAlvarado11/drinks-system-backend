-- Grant the reporting_user read access to sales and inventory schemas
-- Required for the scheduled reporting refresh jobs

GRANT USAGE ON SCHEMA sales TO reporting_user;
GRANT SELECT ON ALL TABLES IN SCHEMA sales TO reporting_user;

GRANT USAGE ON SCHEMA inventory TO reporting_user;
GRANT SELECT ON ALL TABLES IN SCHEMA inventory TO reporting_user;
