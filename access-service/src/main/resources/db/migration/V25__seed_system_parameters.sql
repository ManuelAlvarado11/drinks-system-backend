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
