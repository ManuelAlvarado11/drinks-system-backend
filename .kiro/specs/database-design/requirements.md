# Requirements Document

## Introduction

Complete database design for a bar sales and inventory management system built with a microservices architecture (Spring Boot 4.1 + Angular 17 + PostgreSQL). The database must support four microservices (Access, Sales, Inventory, Reporting) with multi-branch capabilities, RBAC security, cash register management, sales accounts/tabs, inventory control, supplier purchases, audit logging, and reporting. This spec defines all entities, relationships, constraints, and scripts needed before any application code is written.

## Glossary

- **Database_Design**: The complete PostgreSQL schema covering all tables, relationships, indexes, constraints, and scripts for the bar management system
- **Access_Schema**: The set of tables in the Access Service database handling users, roles, permissions, branches, system options, and JWT token management
- **Sales_Schema**: The set of tables in the Sales Service database handling accounts/tabs, sales, sale details, billing, cash registers, and customers
- **Inventory_Schema**: The set of tables in the Inventory Service database handling products, categories, stock, inventory movements, suppliers, and purchase orders
- **Reporting_Schema**: The set of views and support tables in the Reporting Service database enabling dashboards, statistics, and export generation
- **Branch (Sucursal)**: A physical bar location; all operational data is associated to a branch
- **Account (Cuenta)**: An open tab representing an active customer session at a table, identified by client name, table number, or internal code
- **Cash_Register (Caja)**: A cash register session with opening balance, transactions, and closing reconciliation tied to a branch and user
- **Audit_Log (Bitácora)**: A chronological record of all critical system operations including user, timestamp, action, module, and entity affected
- **System_Parameter**: A configurable key-value entry used to avoid hardcoded values throughout the system
- **Soft_Delete**: A deletion strategy using `is_active` and `deleted_at` fields instead of physically removing records
- **Inventory_Movement**: A record of stock change (entry, exit, adjustment, sale, purchase) affecting a product's quantity at a branch

## Requirements

### Requirement 1: Multi-Branch Structure

**User Story:** As a system administrator, I want the database to support multiple branches from the start, so that the system can scale to additional bar locations without schema changes.

#### Acceptance Criteria

1. THE Database_Design SHALL include a `branches` table with fields for id, name, address, phone, email, is_active, created_at, updated_at, created_by, and updated_by
2. THE Database_Design SHALL include a foreign key reference to the branch in all operational tables (users, sales, inventory, cash registers)
3. WHEN a new branch is created, THE Access_Schema SHALL allow associating users to one or more branches
4. THE Database_Design SHALL enforce that inventory stock quantities are tracked per branch
5. THE Database_Design SHALL enforce that sales and accounts are associated to exactly one branch

### Requirement 2: Users, Roles, and Permissions

**User Story:** As a system administrator, I want a permission-based authorization model, so that access control is granular and flexible beyond simple role checks.

#### Acceptance Criteria

1. THE Access_Schema SHALL include tables for `users`, `roles`, `permissions`, `role_permissions`, and `user_roles`
2. THE Access_Schema SHALL define each permission with a unique code, name, description, and module association
3. THE Access_Schema SHALL support a many-to-many relationship between roles and permissions
4. THE Access_Schema SHALL support a many-to-many relationship between users and roles
5. THE Access_Schema SHALL include a `system_menu_options` table linking menu items to required permissions
6. THE Access_Schema SHALL include seed data for initial roles: ADMINISTRADOR_SISTEMA, GERENTE_SUCURSAL, CAJERO
7. THE Access_Schema SHALL store user credentials with fields for username, password_hash, email, full_name, is_active, last_login, branch_id, and audit timestamps
8. THE Access_Schema SHALL include a `user_branches` table to support users assigned to multiple branches

### Requirement 3: JWT Token Management

**User Story:** As a developer, I want token management tables in the database, so that refresh token rotation and revocation are supported server-side.

#### Acceptance Criteria

1. THE Access_Schema SHALL include a `refresh_tokens` table with fields for id, user_id, token_hash, expires_at, is_revoked, created_at, and device_info
2. WHEN a refresh token is revoked, THE Access_Schema SHALL support marking the token as revoked without physical deletion
3. THE Access_Schema SHALL include an index on `user_id` and `token_hash` in the `refresh_tokens` table for fast lookup

### Requirement 4: Cash Register Management

**User Story:** As a cashier, I want a cash register system that tracks opening, closing, deposits, and withdrawals, so that daily cash reconciliation is accurate.

#### Acceptance Criteria

1. THE Sales_Schema SHALL include a `cash_registers` table with fields for id, branch_id, user_id, opening_amount, closing_amount, expected_amount, difference, status (open/closed), opened_at, closed_at, and notes
2. THE Sales_Schema SHALL include a `cash_register_movements` table with fields for id, cash_register_id, movement_type (deposit, withdrawal, sale_income), amount, description, created_at, and created_by
3. WHILE a cash register is in status "open", THE Sales_Schema SHALL allow inserting movements associated to that register
4. WHEN a cash register is closed, THE Sales_Schema SHALL record the closing_amount, expected_amount, and difference
5. THE Sales_Schema SHALL enforce that each cash register is associated to exactly one branch and one user

### Requirement 5: Customer Management

**User Story:** As a cashier, I want to optionally associate sales with registered customers or default to anonymous, so that customer tracking is flexible.

#### Acceptance Criteria

1. THE Sales_Schema SHALL include a `customers` table with fields for id, first_name, last_name, nit_ci, phone, email, is_active, and audit timestamps
2. THE Sales_Schema SHALL include a default "Consumidor Final" record as seed data for anonymous sales
3. THE Sales_Schema SHALL allow the customer_id field in sales to be nullable or default to the "Consumidor Final" record
4. THE Sales_Schema SHALL support querying sales history by customer

### Requirement 6: Products and Categories

**User Story:** As a branch manager, I want products organized in categories with full pricing and stock information, so that the product catalog is well structured.

#### Acceptance Criteria

1. THE Inventory_Schema SHALL include a `categories` table with fields for id, name, description, parent_category_id (self-referencing for hierarchy), is_active, and audit timestamps
2. THE Inventory_Schema SHALL include a `products` table with fields for id, code (unique), name, category_id, size, description, cost_price, sale_price, is_active, and audit timestamps
3. THE Inventory_Schema SHALL include a `product_stock` table with fields for id, product_id, branch_id, current_stock, minimum_stock to track inventory per product per branch
4. THE Inventory_Schema SHALL enforce a unique constraint on the combination of product_id and branch_id in the stock table
5. THE Inventory_Schema SHALL support category hierarchy through the self-referencing parent_category_id field

### Requirement 7: Inventory Movements

**User Story:** As a branch manager, I want a complete history of all inventory movements, so that stock changes are traceable and auditable.

#### Acceptance Criteria

1. THE Inventory_Schema SHALL include an `inventory_movements` table with fields for id, product_id, branch_id, movement_type, quantity, previous_stock, new_stock, reference_type, reference_id, notes, created_at, and created_by
2. THE Inventory_Schema SHALL support movement types: ENTRY, EXIT, ADJUSTMENT, SALE, PURCHASE, TRANSFER
3. WHEN a sale is completed, THE Inventory_Schema SHALL record EXIT movements for each product sold
4. WHEN a purchase is received, THE Inventory_Schema SHALL record ENTRY movements for each product purchased
5. THE Inventory_Schema SHALL store the previous_stock and new_stock values in each movement record for audit trail
6. THE Inventory_Schema SHALL include indexes on product_id, branch_id, and movement_type for efficient querying

### Requirement 8: Sales Accounts and Tabs

**User Story:** As a cashier, I want to manage open accounts (tabs) that track drinks added over time, so that customers can pay at the end of their visit.

#### Acceptance Criteria

1. THE Sales_Schema SHALL include an `accounts` table with fields for id, branch_id, customer_name, customer_last_name, table_number, internal_code, status (open/closed), opened_at, closed_at, opened_by, closed_by, and notes
2. THE Sales_Schema SHALL include an `account_details` table with fields for id, account_id, product_id, quantity, unit_price, subtotal, added_at, added_by, and is_cancelled
3. WHEN a product is added to an open account, THE Sales_Schema SHALL record the detail with timestamp and user who added it
4. WHEN a product is removed from an open account, THE Sales_Schema SHALL mark the detail as cancelled rather than physically deleting it
5. THE Sales_Schema SHALL support querying open accounts with elapsed time (calculated from opened_at) and partial total (sum of active details)
6. WHEN an account is closed, THE Sales_Schema SHALL update the status to "closed" and record closed_at and closed_by

### Requirement 9: Sales and Billing

**User Story:** As a cashier, I want to generate a formal sale record when closing an account, so that billing and financial records are complete.

#### Acceptance Criteria

1. THE Sales_Schema SHALL include a `sales` table with fields for id, branch_id, account_id, customer_id, cash_register_id, sale_number (sequential), subtotal, discount_amount, tax_amount, total_amount, payment_method, status, sale_date, created_by, and audit timestamps
2. THE Sales_Schema SHALL include a `sale_details` table with fields for id, sale_id, product_id, quantity, unit_price, subtotal, and discount
3. WHEN an account is closed, THE Sales_Schema SHALL create a sale record linked to the account
4. THE Sales_Schema SHALL support payment methods as a configurable catalog (cash, card, transfer, mixed)
5. THE Sales_Schema SHALL generate sequential sale numbers per branch
6. THE Sales_Schema SHALL support a discount_amount field at both the sale level and the detail level

### Requirement 10: Supplier and Purchase Management

**User Story:** As a branch manager, I want to manage suppliers and create purchase orders that automatically increase inventory, so that restocking is traceable.

#### Acceptance Criteria

1. THE Inventory_Schema SHALL include a `suppliers` table with fields for id, name, contact_name, phone, email, address, nit, is_active, and audit timestamps
2. THE Inventory_Schema SHALL include a `purchase_orders` table with fields for id, supplier_id, branch_id, order_number, status (pending, received, cancelled), total_amount, order_date, received_date, created_by, and audit timestamps
3. THE Inventory_Schema SHALL include a `purchase_order_details` table with fields for id, purchase_order_id, product_id, quantity_ordered, quantity_received, unit_cost, subtotal
4. WHEN a purchase order is marked as received, THE Inventory_Schema SHALL allow recording the received quantities per product
5. THE Inventory_Schema SHALL support partial deliveries by tracking quantity_ordered vs quantity_received separately

### Requirement 11: Audit Log

**User Story:** As a system administrator, I want all critical operations logged in a bitácora, so that user activity is fully traceable.

#### Acceptance Criteria

1. THE Database_Design SHALL include an `audit_logs` table with fields for id, user_id, username, action, module, entity_name, entity_id, old_values (JSONB), new_values (JSONB), ip_address, timestamp, and description
2. THE Database_Design SHALL store old and new values as JSONB for flexible schema evolution
3. THE Database_Design SHALL include indexes on user_id, module, entity_name, and timestamp for efficient log querying
4. THE Database_Design SHALL partition or archive audit logs by date to manage table growth

### Requirement 12: System Parameters and Configurable Catalogs

**User Story:** As a system administrator, I want system parameters and catalogs stored in the database, so that behavior changes do not require code deployments.

#### Acceptance Criteria

1. THE Database_Design SHALL include a `system_parameters` table with fields for id, parameter_key (unique), parameter_value, data_type, description, module, is_active, and audit timestamps
2. THE Database_Design SHALL include a `catalogs` table with fields for id, catalog_type, code, name, description, sort_order, is_active, and parent_id for hierarchical catalogs
3. THE Database_Design SHALL use the catalogs table for payment methods, movement types, account statuses, and other enumerable values
4. THE Database_Design SHALL enforce a unique constraint on the combination of catalog_type and code

### Requirement 13: Notifications Structure

**User Story:** As a branch manager, I want to receive low stock notifications, so that I can reorder products before running out.

#### Acceptance Criteria

1. THE Database_Design SHALL include a `notifications` table with fields for id, branch_id, user_id, notification_type, title, message, entity_name, entity_id, is_read, created_at, and read_at
2. WHEN a product's current_stock falls below its minimum_stock, THE Inventory_Schema SHALL support generating a low stock notification
3. THE Database_Design SHALL include an index on user_id and is_read for efficient unread notification queries

### Requirement 14: Reporting Support Structures

**User Story:** As a manager, I want the database to support efficient reporting queries, so that dashboards and exports load quickly.

#### Acceptance Criteria

1. THE Reporting_Schema SHALL include materialized views or summary tables for daily sales totals per branch
2. THE Reporting_Schema SHALL include materialized views or summary tables for monthly sales totals per branch
3. THE Reporting_Schema SHALL support querying best-selling products by branch and date range
4. THE Reporting_Schema SHALL support querying current inventory status across all branches
5. THE Reporting_Schema SHALL support querying profit calculations (sale_price - cost_price) per product and period
6. THE Database_Design SHALL include appropriate indexes on date columns and branch_id in sales and inventory tables to optimize report generation

### Requirement 15: Database Design Standards and Conventions

**User Story:** As a developer, I want consistent design standards across all schemas, so that the codebase is maintainable and predictable.

#### Acceptance Criteria

1. THE Database_Design SHALL use PostgreSQL as the database engine for all microservices
2. THE Database_Design SHALL include audit timestamp columns (created_at, updated_at) on all tables
3. THE Database_Design SHALL include audit user columns (created_by, updated_by) on all modifiable tables
4. THE Database_Design SHALL implement soft delete using is_active (boolean) and deleted_at (timestamp) columns on entities that require historical preservation
5. THE Database_Design SHALL use UUID or BIGSERIAL for primary keys consistently across all tables
6. THE Database_Design SHALL define foreign key constraints with appropriate ON DELETE behavior (RESTRICT or SET NULL depending on context)
7. THE Database_Design SHALL use snake_case naming convention for all tables and columns
8. THE Database_Design SHALL include NOT NULL constraints on all required fields
9. THE Database_Design SHALL include CHECK constraints for enumerable status fields

### Requirement 16: Indexing and Performance

**User Story:** As a developer, I want proper indexes defined from the start, so that queries perform well under load.

#### Acceptance Criteria

1. THE Database_Design SHALL include indexes on all foreign key columns
2. THE Database_Design SHALL include composite indexes on frequently joined columns (branch_id + status, product_id + branch_id)
3. THE Database_Design SHALL include partial indexes on is_active = true for tables using soft delete
4. THE Database_Design SHALL include indexes on date/timestamp columns used in reporting queries (sale_date, created_at, opened_at)
5. THE Database_Design SHALL document the justification for each non-obvious index

### Requirement 17: Schema Separation per Microservice

**User Story:** As a developer, I want clear schema boundaries per microservice, so that each service owns its data independently.

#### Acceptance Criteria

1. THE Database_Design SHALL define separate PostgreSQL schemas (or databases) for each microservice: access, sales, inventory, reporting
2. THE Database_Design SHALL avoid cross-schema foreign keys; shared references use IDs without enforced FK constraints across service boundaries
3. THE Database_Design SHALL document which entity IDs are shared across service boundaries and how referential integrity is maintained at the application level
4. THE Database_Design SHALL include a shared `branches` reference in each schema that needs branch context

### Requirement 18: Seed Data and Initial Configuration

**User Story:** As a developer, I want seed data scripts included in the design, so that the system starts with a usable default configuration.

#### Acceptance Criteria

1. THE Database_Design SHALL include SQL seed scripts for initial roles (ADMINISTRADOR_SISTEMA, GERENTE_SUCURSAL, CAJERO) with their default permissions
2. THE Database_Design SHALL include SQL seed scripts for the default "Consumidor Final" customer record
3. THE Database_Design SHALL include SQL seed scripts for system parameters (tax rate, default currency, ticket format settings)
4. THE Database_Design SHALL include SQL seed scripts for catalogs (payment methods, movement types, notification types)
5. THE Database_Design SHALL include a default admin user for initial system access

### Requirement 19: Deployment Readiness

**User Story:** As a DevOps engineer, I want the database scripts ready for containerized deployment, so that environments are reproducible.

#### Acceptance Criteria

1. THE Database_Design SHALL provide DDL scripts organized by schema (one file per schema or logically grouped)
2. THE Database_Design SHALL provide DML scripts for seed data separate from DDL scripts
3. THE Database_Design SHALL include schema versioning support compatible with Flyway or Liquibase migration tools
4. THE Database_Design SHALL use environment-variable placeholders for connection strings and credentials in deployment scripts
5. THE Database_Design SHALL be compatible with Docker-based PostgreSQL deployment
