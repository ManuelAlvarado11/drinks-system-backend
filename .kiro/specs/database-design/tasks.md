# Plan de Implementación: Diseño de Base de Datos del Sistema de Gestión de Bar

## Visión General

Este plan cubre la implementación completa de la base de datos PostgreSQL (`drinks_db`) con 4 esquemas, incluyendo infraestructura Docker, scripts de migración Flyway (V0-V27), configuración Spring Boot, y tests de validación con Testcontainers. El lenguaje de implementación es Java (Spring Boot) con SQL para scripts DDL/DML.

## Tareas

- [x] 1. Configurar infraestructura Docker y entorno base
  - [x] 1.1 Crear archivo Docker Compose con servicio PostgreSQL 16-alpine
    - Crear `docker-compose.yml` con servicio `postgres-drinks`, volumen persistente, healthcheck, y mapeo de puertos
    - Configurar variables de entorno para `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
    - _Requisitos: 19.5, 19.4_

  - [x] 1.2 Crear script de inicialización de esquemas y usuarios por servicio
    - Crear `docker/init-schemas.sql` con creación de esquemas (`access`, `sales`, `inventory`, `reporting`)
    - Crear usuarios por servicio (`access_user`, `sales_user`, `inventory_user`, `reporting_user`)
    - Definir permisos GRANT/REVOKE para cada usuario sobre su esquema propio y lectura de esquemas compartidos
    - _Requisitos: 17.1, 19.4, 19.5_

  - [x] 1.3 Crear archivos de configuración de entorno
    - Crear `.env.example` con todas las variables de entorno necesarias (DB_HOST, DB_PORT, DB_NAME, credenciales por servicio)
    - Crear `.env` local para desarrollo (con valores default)
    - _Requisitos: 19.4_

- [x] 2. Crear scripts de migración Flyway — Schema `access` (V0-V8)
  - [x] 2.1 Crear migración V0 — Creación de esquemas
    - Crear `db/migration/V0__create_schemas.sql` con `CREATE SCHEMA IF NOT EXISTS` para los 4 esquemas
    - _Requisitos: 17.1, 19.1, 19.3_

  - [x] 2.2 Crear migración V1 — Tabla `access.branches`
    - Crear `db/migration/V1__access_create_branches.sql` con DDL completo, columnas de auditoría, soft delete, e índice parcial en `is_active`
    - _Requisitos: 1.1, 15.2, 15.3, 15.4, 15.7, 16.3_

  - [x] 2.3 Crear migraciones V2 — Tablas de usuarios, roles y permisos
    - Crear `db/migration/V2__access_create_users_roles_permissions.sql` con tablas: `users`, `roles`, `permissions`, `role_permissions`, `user_roles`, `user_branches`
    - Incluir todas las FK, constraints UNIQUE, índices en FK columns, partial indexes en `is_active`
    - _Requisitos: 2.1, 2.2, 2.3, 2.4, 2.7, 2.8, 15.5, 15.6, 15.7, 15.8, 16.1_

  - [x] 2.4 Crear migración V3 — Tabla `access.refresh_tokens`
    - Crear `db/migration/V3__access_create_refresh_tokens.sql` con DDL completo, índices en `user_id`, `token_hash`, y `expires_at`
    - _Requisitos: 3.1, 3.2, 3.3, 16.1_

  - [x] 2.5 Crear migración V4 — Tabla `access.system_menu_options`
    - Crear `db/migration/V4__access_create_menu_options.sql` con DDL completo, auto-referencia `parent_id`, FK a `permissions`
    - _Requisitos: 2.5, 15.6_

  - [x] 2.6 Crear migración V5 — Tabla `access.system_parameters`
    - Crear `db/migration/V5__access_create_system_parameters.sql` con DDL completo, constraint CHECK en `data_type`, índices en `module` y `parameter_key`
    - _Requisitos: 12.1, 15.8, 15.9_

  - [x] 2.7 Crear migración V6 — Tabla `access.catalogs`
    - Crear `db/migration/V6__access_create_catalogs.sql` con DDL completo, constraint UNIQUE en `(catalog_type, code)`, auto-referencia `parent_id`
    - _Requisitos: 12.2, 12.3, 12.4, 15.9_

  - [x] 2.8 Crear migración V7 — Tabla `access.audit_logs` (particionada)
    - Crear `db/migration/V7__access_create_audit_logs.sql` con DDL particionado por rango (`PARTITION BY RANGE (created_at)`), partición default, índices en `user_id`, `module`, `entity_name`, `created_at`, `action`
    - _Requisitos: 11.1, 11.2, 11.3, 11.4, 16.4_

  - [x] 2.9 Crear migración V8 — Tabla `access.notifications`
    - Crear `db/migration/V8__access_create_notifications.sql` con DDL completo, FK a `branches` y `users`, índice compuesto en `(user_id, is_read)`
    - _Requisitos: 13.1, 13.3, 16.2_

- [x] 3. Checkpoint — Verificar migraciones del schema `access`
  - Ensure all tests pass, ask the user if questions arise.

- [x] 4. Crear scripts de migración Flyway — Schema `inventory` (V9-V14)
  - [x] 4.1 Crear migración V9 — Tabla `inventory.categories`
    - Crear `db/migration/V9__inventory_create_categories.sql` con DDL completo, auto-referencia `parent_category_id`, soft delete, partial index
    - _Requisitos: 6.1, 6.5, 15.4, 16.3_

  - [x] 4.2 Crear migración V10 — Tabla `inventory.products`
    - Crear `db/migration/V10__inventory_create_products.sql` con DDL completo, FK a `categories`, constraint UNIQUE en `code`, índices en `category_id`, `code`, `is_active`, `name`
    - _Requisitos: 6.2, 15.4, 15.7, 15.8, 16.1, 16.3_

  - [x] 4.3 Crear migración V11 — Tabla `inventory.product_stock`
    - Crear `db/migration/V11__inventory_create_product_stock.sql` con DDL completo, FK cruzada a `access.branches`, constraint UNIQUE en `(product_id, branch_id)`, partial index para stock bajo
    - _Requisitos: 1.4, 6.3, 6.4, 16.2, 16.3_

  - [x] 4.4 Crear migración V12 — Tabla `inventory.inventory_movements`
    - Crear `db/migration/V12__inventory_create_inventory_movements.sql` con DDL completo, FK cruzada a `access.branches`, CHECK constraint en `movement_type`, índices en `product_id`, `branch_id`, `movement_type`, composite `(product_id, branch_id)`, `created_at`
    - _Requisitos: 7.1, 7.2, 7.5, 7.6, 15.9, 16.1, 16.2, 16.4_

  - [x] 4.5 Crear migración V13 — Tabla `inventory.suppliers`
    - Crear `db/migration/V13__inventory_create_suppliers.sql` con DDL completo, soft delete, partial index en `is_active`, índice en `nit`
    - _Requisitos: 10.1, 15.4, 16.3_

  - [x] 4.6 Crear migración V14 — Tablas `inventory.purchase_orders` y `purchase_order_details`
    - Crear `db/migration/V14__inventory_create_purchase_orders.sql` con DDL completo de ambas tablas, FK a `suppliers`, FK cruzada a `access.branches`, CHECK constraints en `status`, `quantity_ordered`, `quantity_received`
    - _Requisitos: 10.2, 10.3, 10.4, 10.5, 15.6, 15.9, 16.1_

- [x] 5. Crear scripts de migración Flyway — Schema `sales` (V15-V18)
  - [x] 5.1 Crear migración V15 — Tabla `sales.customers`
    - Crear `db/migration/V15__sales_create_customers.sql` con DDL completo, soft delete, índices en `nit_ci`, `(first_name, last_name)`, partial index en `is_active`
    - _Requisitos: 5.1, 15.4, 16.1, 16.3_

  - [x] 5.2 Crear migración V16 — Tablas `sales.cash_registers` y `cash_register_movements`
    - Crear `db/migration/V16__sales_create_cash_registers.sql` con DDL completo de ambas tablas, FK cruzada a `access.branches`, CHECK constraints en `status` y `movement_type`, índices compuestos en `(branch_id, status)`
    - _Requisitos: 4.1, 4.2, 4.5, 1.5, 15.9, 16.1, 16.2_

  - [x] 5.3 Crear migración V17 — Tablas `sales.accounts` y `account_details`
    - Crear `db/migration/V17__sales_create_accounts.sql` con DDL completo de ambas tablas, FK cruzada a `access.branches` y `inventory.products`, CHECK constraint en `status`, CHECK en `quantity > 0`
    - _Requisitos: 8.1, 8.2, 8.4, 1.5, 15.6, 15.8, 15.9, 16.1, 16.2_

  - [x] 5.4 Crear migración V18 — Tablas `sales.sales` y `sale_details`
    - Crear `db/migration/V18__sales_create_sales.sql` con DDL completo de ambas tablas, FK cruzadas a `access.branches` e `inventory.products`, UNIQUE index en `(branch_id, sale_number)`, índices compuestos para reportes
    - _Requisitos: 9.1, 9.2, 9.5, 1.5, 15.5, 15.6, 15.9, 16.1, 16.2, 16.4_

- [x] 6. Crear scripts de migración Flyway — Schema `reporting` (V19-V22)
  - [x] 6.1 Crear migración V19 — Tabla `reporting.daily_sales_summary`
    - Crear `db/migration/V19__reporting_create_daily_sales_summary.sql` con DDL completo, FK cruzada a `access.branches`, UNIQUE en `(branch_id, summary_date)`, índices en `branch_id`, `summary_date`
    - _Requisitos: 14.1, 14.6, 16.2_

  - [x] 6.2 Crear migración V20 — Tabla `reporting.monthly_sales_summary`
    - Crear `db/migration/V20__reporting_create_monthly_sales_summary.sql` con DDL completo, CHECK en `month BETWEEN 1 AND 12`, UNIQUE en `(branch_id, year, month)`
    - _Requisitos: 14.2, 14.6_

  - [x] 6.3 Crear migración V21 — Tabla `reporting.product_sales_ranking`
    - Crear `db/migration/V21__reporting_create_product_sales_ranking.sql` con DDL completo, FK cruzadas a `inventory.products` y `access.branches`, índice DESC en `total_revenue`
    - _Requisitos: 14.3, 14.5, 16.2_

  - [x] 6.4 Crear migración V22 — Tabla `reporting.inventory_status_view`
    - Crear `db/migration/V22__reporting_create_inventory_status_view.sql` con DDL completo, partial index en `is_low_stock = true`
    - _Requisitos: 14.4, 16.3_

- [x] 7. Checkpoint — Verificar migraciones de todos los esquemas
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Crear scripts de seed data (V23-V27)
  - [x] 8.1 Crear migración V23 — Seed de roles y permisos
    - Crear `db/migration/V23__seed_roles_permissions.sql` con INSERT de roles iniciales (ADMINISTRADOR_SISTEMA, GERENTE_SUCURSAL, CAJERO), permisos por módulo, y asignación de permisos a roles
    - _Requisitos: 2.6, 18.1_

  - [x] 8.2 Crear migración V24 — Seed de usuario admin
    - Crear `db/migration/V24__seed_admin_user.sql` con INSERT del usuario admin con password hash BCrypt y asignación del rol ADMINISTRADOR_SISTEMA
    - _Requisitos: 18.5_

  - [x] 8.3 Crear migración V25 — Seed de parámetros del sistema
    - Crear `db/migration/V25__seed_system_parameters.sql` con INSERT de parámetros iniciales (TAX_RATE, DEFAULT_CURRENCY, TICKET_HEADER, TICKET_FOOTER, LOW_STOCK_THRESHOLD, SESSION_TIMEOUT, etc.)
    - _Requisitos: 18.3_

  - [x] 8.4 Crear migración V26 — Seed de catálogos
    - Crear `db/migration/V26__seed_catalogs.sql` con INSERT de catálogos: métodos de pago, tipos de movimiento de inventario, tipos de notificación, estados de cuenta, estados de orden de compra
    - _Requisitos: 12.3, 18.4_

  - [x] 8.5 Crear migración V27 — Seed de cliente por defecto
    - Crear `db/migration/V27__seed_default_customer.sql` con INSERT de "Consumidor Final" y ajuste de secuencia
    - _Requisitos: 5.2, 18.2_

- [x] 9. Configurar Flyway y Spring Boot para cada microservicio
  - [x] 9.1 Configurar Flyway en Access Service (servicio principal de migraciones)
    - Crear/modificar `access-service/src/main/resources/application.yml` con configuración Flyway habilitada, locations apuntando a `db/migration`, schemas listados en orden, `hibernate.default_schema=access`, `ddl-auto=validate`
    - Agregar dependencia de Flyway y PostgreSQL driver en `pom.xml`
    - _Requisitos: 19.3_

  - [x] 9.2 Configurar Spring Boot para Sales Service
    - Crear `application.yml` para Sales Service con Flyway deshabilitado, `hibernate.default_schema=sales`, `ddl-auto=validate`, datasource apuntando a `drinks_db` con `sales_user`
    - _Requisitos: 17.1, 19.4_

  - [x] 9.3 Configurar Spring Boot para Inventory Service
    - Crear `application.yml` para Inventory Service con Flyway deshabilitado, `hibernate.default_schema=inventory`, `ddl-auto=validate`, datasource apuntando a `drinks_db` con `inventory_user`
    - _Requisitos: 17.1, 19.4_

  - [x] 9.4 Configurar Spring Boot para Reporting Service
    - Crear `application.yml` para Reporting Service con Flyway deshabilitado, `hibernate.default_schema=reporting`, `ddl-auto=validate`, datasource apuntando a `drinks_db` con `reporting_user`
    - _Requisitos: 17.1, 19.4_

- [x] 10. Checkpoint — Verificar configuración de servicios
  - Ensure all tests pass, ask the user if questions arise.

- [x] 11. Configurar infraestructura de testing con Testcontainers
  - [x] 11.1 Agregar dependencias de testing en `pom.xml`
    - Agregar dependencias: Testcontainers (postgresql module), Spring Boot Test, JUnit 5, AssertJ, Flyway Test Extensions
    - Configurar profile de test con Testcontainers
    - _Requisitos: 19.3, 19.5_

  - [x] 11.2 Crear clase base de test con Testcontainers
    - Crear clase abstracta `AbstractDatabaseIntegrationTest` que levante un contenedor PostgreSQL con Testcontainers, ejecute Flyway migrations, y configure datasource dinámico
    - _Requisitos: 19.5_

- [x] 12. Implementar tests de validación de esquema
  - [x] 12.1 Crear tests de validación de estructura de esquemas
    - Crear `SchemaValidationTest.java` que verifique existencia de los 4 esquemas, existencia de todas las tablas por esquema, y tipos de columna correctos via `DatabaseMetaData`
    - _Requisitos: 17.1, 15.1, 15.7_

  - [x] 12.2 Crear tests de validación de constraints
    - Crear `ConstraintValidationTest.java` que verifique constraints UNIQUE (insertar duplicados y esperar error), CHECK constraints (insertar valores inválidos de status), NOT NULL constraints
    - _Requisitos: 15.8, 15.9, 12.4, 6.4_

  - [x] 12.3 Crear tests de validación de índices
    - Crear `IndexValidationTest.java` que verifique existencia de todos los índices definidos en el diseño, incluyendo partial indexes y composite indexes
    - _Requisitos: 16.1, 16.2, 16.3, 16.4, 16.5_

- [x] 13. Implementar tests de migración Flyway
  - [x] 13.1 Crear tests de ejecución de migraciones
    - Crear `FlywayMigrationTest.java` que verifique que todas las migraciones se ejecutan sin errores, no quedan migraciones pendientes, y el orden de creación es correcto (access → inventory → sales → reporting)
    - _Requisitos: 19.3_

  - [x] 13.2 Crear tests de idempotencia de migraciones
    - Crear test que verifique que ejecutar Flyway `validate` después de `migrate` pasa sin errores, confirmando consistencia entre scripts y estado de la DB
    - _Requisitos: 19.3_

- [x] 14. Implementar tests de integridad referencial
  - [x] 14.1 Crear tests de FK cruzadas entre esquemas
    - Crear `CrossSchemaForeignKeyTest.java` que verifique: `sales.cash_registers.branch_id` → `access.branches`, `sales.account_details.product_id` → `inventory.products`, `inventory.product_stock.branch_id` → `access.branches`
    - Verificar que INSERT con FK inexistente falla con error apropiado
    - _Requisitos: 15.6, 1.2_

  - [x] 14.2 Crear tests de comportamiento ON DELETE
    - Crear `DeleteBehaviorTest.java` que verifique: RESTRICT impide eliminar branch con cajas asociadas, CASCADE elimina `user_roles` al eliminar user, SET NULL anula `branch_id` en users al eliminar branch
    - _Requisitos: 15.6_

  - [x] 14.3 Crear tests de seed data
    - Crear `SeedDataTest.java` que verifique: existencia de roles iniciales, existencia de permisos por módulo, usuario admin creado, parámetros del sistema presentes, catálogos insertados, cliente "Consumidor Final" existe
    - _Requisitos: 18.1, 18.2, 18.3, 18.4, 18.5_

- [x] 15. Checkpoint final — Verificar que toda la suite de tests pasa
  - Ensure all tests pass, ask the user if questions arise.

## Notas

- Las tareas siguen el orden de dependencia: Docker → access (V0-V8) → inventory (V9-V14) → sales (V15-V18) → reporting (V19-V22) → seed data (V23-V27) → configuración → tests
- El Access Service es el único responsable de ejecutar migraciones Flyway; los demás servicios usan `ddl-auto: validate`
- Todos los scripts SQL deben iniciar con `SET search_path TO {schema}` para claridad
- Los tests utilizan Testcontainers para levantar PostgreSQL real (no H2) y garantizar compatibilidad
- Checkpoints permiten validación incremental antes de continuar con fases siguientes
- Los scripts de seed data (V23-V27) están separados de DDL para facilitar mantenimiento

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.3"] },
    { "id": 1, "tasks": ["1.2"] },
    { "id": 2, "tasks": ["2.1"] },
    { "id": 3, "tasks": ["2.2"] },
    { "id": 4, "tasks": ["2.3", "2.4", "2.5", "2.6", "2.7"] },
    { "id": 5, "tasks": ["2.8", "2.9"] },
    { "id": 6, "tasks": ["4.1"] },
    { "id": 7, "tasks": ["4.2"] },
    { "id": 8, "tasks": ["4.3", "4.4", "4.5"] },
    { "id": 9, "tasks": ["4.6"] },
    { "id": 10, "tasks": ["5.1", "5.2"] },
    { "id": 11, "tasks": ["5.3", "5.4"] },
    { "id": 12, "tasks": ["6.1", "6.2", "6.3", "6.4"] },
    { "id": 13, "tasks": ["8.1"] },
    { "id": 14, "tasks": ["8.2", "8.3", "8.4", "8.5"] },
    { "id": 15, "tasks": ["9.1"] },
    { "id": 16, "tasks": ["9.2", "9.3", "9.4"] },
    { "id": 17, "tasks": ["11.1"] },
    { "id": 18, "tasks": ["11.2"] },
    { "id": 19, "tasks": ["12.1", "12.2", "12.3", "13.1", "13.2"] },
    { "id": 20, "tasks": ["14.1", "14.2", "14.3"] }
  ]
}
```
