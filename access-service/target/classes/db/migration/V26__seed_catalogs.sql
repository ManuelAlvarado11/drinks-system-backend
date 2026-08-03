-- V26__seed_catalogs.sql
-- Seed de catálogos del sistema: métodos de pago, tipos de movimiento,
-- tipos de notificación, estados de cuenta, estados de orden de compra

SET search_path TO access;

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
