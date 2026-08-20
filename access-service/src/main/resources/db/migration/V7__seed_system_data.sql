-- ============================================================================
-- V7__seed_system_data.sql
-- System parameters, catalogs, and default customer
-- ============================================================================

-- =============================================================================
-- SYSTEM PARAMETERS
-- =============================================================================
SET search_path TO access;

INSERT INTO access.system_parameters (parameter_key, parameter_value, data_type, description, module) VALUES
('TAX_RATE', '13', 'DECIMAL', 'Tasa de impuesto IVA en porcentaje', 'VENTAS'),
('DEFAULT_CURRENCY', 'BOB', 'STRING', 'Moneda por defecto (Bolivianos)', 'GENERAL'),
('TICKET_HEADER', 'BAR DRINKS SYSTEM', 'STRING', 'Encabezado del ticket de venta', 'VENTAS'),
('TICKET_FOOTER', 'Gracias por su preferencia', 'STRING', 'Pie de página del ticket', 'VENTAS'),
('LOW_STOCK_THRESHOLD', '5', 'INTEGER', 'Umbral global de stock bajo si no se define por producto', 'INVENTARIO'),
('SESSION_TIMEOUT_MINUTES', '480', 'INTEGER', 'Timeout de sesión en minutos', 'SEGURIDAD'),
('MAX_OPEN_ACCOUNTS_PER_BRANCH', '50', 'INTEGER', 'Máximo de cuentas abiertas simultáneas por sucursal', 'VENTAS'),
('SALE_NUMBER_PREFIX', 'VTA', 'STRING', 'Prefijo para números de venta', 'VENTAS');

-- =============================================================================
-- CATALOGS
-- =============================================================================

-- Métodos de pago
INSERT INTO access.catalogs (catalog_type, code, name, sort_order) VALUES
('PAYMENT_METHOD', 'CASH', 'Efectivo', 1),
('PAYMENT_METHOD', 'CARD', 'Tarjeta', 2),
('PAYMENT_METHOD', 'TRANSFER', 'Transferencia', 3),
('PAYMENT_METHOD', 'MIXED', 'Mixto', 4),
('PAYMENT_METHOD', 'QR', 'Pago QR', 5);

-- Tipos de movimiento de inventario
INSERT INTO access.catalogs (catalog_type, code, name, sort_order) VALUES
('MOVEMENT_TYPE', 'ENTRY', 'Entrada', 1),
('MOVEMENT_TYPE', 'EXIT', 'Salida', 2),
('MOVEMENT_TYPE', 'ADJUSTMENT', 'Ajuste', 3),
('MOVEMENT_TYPE', 'SALE', 'Venta', 4),
('MOVEMENT_TYPE', 'PURCHASE', 'Compra', 5),
('MOVEMENT_TYPE', 'TRANSFER', 'Transferencia', 6);

-- Tipos de notificación
INSERT INTO access.catalogs (catalog_type, code, name, sort_order) VALUES
('NOTIFICATION_TYPE', 'LOW_STOCK', 'Stock bajo', 1),
('NOTIFICATION_TYPE', 'CASH_CLOSED', 'Caja cerrada', 2),
('NOTIFICATION_TYPE', 'ORDER_RECEIVED', 'Orden recibida', 3),
('NOTIFICATION_TYPE', 'SYSTEM_ALERT', 'Alerta del sistema', 4);

-- Estados de cuenta
INSERT INTO access.catalogs (catalog_type, code, name, sort_order) VALUES
('ACCOUNT_STATUS', 'OPEN', 'Abierta', 1),
('ACCOUNT_STATUS', 'CLOSED', 'Cerrada', 2),
('ACCOUNT_STATUS', 'CANCELLED', 'Anulada', 3);

-- Estados de orden de compra
INSERT INTO access.catalogs (catalog_type, code, name, sort_order) VALUES
('PURCHASE_STATUS', 'PENDING', 'Pendiente', 1),
('PURCHASE_STATUS', 'RECEIVED', 'Recibida', 2),
('PURCHASE_STATUS', 'PARTIAL', 'Parcial', 3),
('PURCHASE_STATUS', 'CANCELLED', 'Cancelada', 4);

-- =============================================================================
-- DEFAULT CUSTOMER (Consumidor Final)
-- =============================================================================
SET search_path TO sales;

INSERT INTO sales.customers (id, first_name, last_name, nit_ci, is_active)
VALUES (1, 'Consumidor', 'Final', '0', true);

SELECT setval('sales.customers_id_seq', 1, true);
