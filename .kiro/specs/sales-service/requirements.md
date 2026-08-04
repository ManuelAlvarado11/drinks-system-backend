# Documento de Requerimientos: Sales Service

## Introducción

El Sales Service es el microservicio responsable de la gestión de ventas del Sistema de Gestión de Ventas e Inventario de Bar. Opera sobre el esquema `sales` de la base de datos PostgreSQL `drinks_db` y expone endpoints REST bajo el prefijo `/api/sales/v1/`. Este servicio implementa la lógica de negocio para gestión de clientes, cajas registradoras, cuentas/comandas (tabs), y ventas con facturación. Utiliza arquitectura hexagonal con Spring Boot 4.1, Java 17 y se comunica con el Inventory Service para deducción de stock. Toda operación requiere autenticación JWT (emitido por el Access Service).

## Glosario

- **Sales_Service**: Microservicio Spring Boot que gestiona ventas, cuentas, cajas y clientes, escuchando en el puerto 8082
- **Customer (Cliente)**: Persona que realiza compras en el bar; identificado por nombre, NIT/CI, teléfono
- **Cash_Register (Caja)**: Sesión de caja con monto de apertura, movimientos y cierre con arqueo vinculada a sucursal y usuario
- **Cash_Register_Movement (Movimiento de Caja)**: Registro de entrada/salida de efectivo: DEPOSIT, WITHDRAWAL, SALE_INCOME
- **Account (Cuenta)**: Tab abierto que representa una sesión activa de consumo; identificado por nombre de cliente, mesa o código interno
- **Account_Detail (Detalle de Cuenta)**: Línea de producto agregada a una cuenta con cantidad, precio unitario y subtotal
- **Sale (Venta)**: Transacción de venta cerrada con totales, método de pago, número de venta y estado
- **Sale_Detail (Detalle de Venta)**: Línea de producto de una venta con cantidad, precio unitario, subtotal y descuento
- **Stock_Deduction**: Llamada al Inventory Service para decrementar stock cuando se confirma una venta
- **Sale_Number**: Identificador secuencial único por sucursal para cada venta (format: VTA-YYYYMMDD-XXXX)

## Requerimientos

### Requerimiento 1: Gestión de Clientes

**User Story:** Como cajero, quiero registrar y consultar clientes para asociarlos a ventas y emitir facturas con sus datos.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/customers para crear clientes con campos: firstName, lastName, nitCi, phone, email
2. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/customers para listar clientes paginado con filtro por búsqueda (nombre o NIT)
3. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/customers/{id} para obtener detalle de un cliente
4. EL Sales_Service DEBE exponer un endpoint PUT /api/sales/v1/customers/{id} para actualizar datos del cliente
5. EL Sales_Service DEBE exponer un endpoint DELETE /api/sales/v1/customers/{id} para soft-delete (isActive=false)
6. EL Sales_Service DEBE exigir permiso SALES_CUSTOMERS para operaciones CRUD de clientes
7. EL Sales_Service DEBE incluir un cliente por defecto "Consumidor Final" (id=1, NIT=0) para ventas sin cliente

### Requerimiento 2: Apertura de Caja Registradora

**User Story:** Como cajero, quiero abrir una caja registradora con un monto inicial para iniciar mi turno de trabajo.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/cash-registers para abrir una caja con campos: branchId, openingAmount
2. EL sistema DEBE validar que el usuario no tenga otra caja ABIERTA en la misma sucursal
3. EL estado inicial de la caja DEBE ser 'OPEN'
4. EL Sales_Service DEBE registrar userId y branchId automáticamente del JWT del usuario autenticado
5. EL Sales_Service DEBE exigir permiso CASH_REGISTERS_OPEN para abrir una caja

### Requerimiento 3: Movimientos de Caja

**User Story:** Como cajero, quiero registrar depósitos y retiros de efectivo de la caja para mantener el control del dinero.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/cash-registers/{id}/movements para registrar un movimiento con campos: movementType (DEPOSIT, WITHDRAWAL), amount, description
2. EL sistema DEBE validar que la caja esté en estado OPEN para aceptar movimientos
3. EL sistema NO debe permitir movimientos tipo SALE_INCOME manualmente (se generan automáticamente al confirmar una venta)
4. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/cash-registers/{id}/movements para listar movimientos de una caja
5. EL Sales_Service DEBE exigir permiso CASH_REGISTERS_MOVEMENTS para gestionar movimientos

### Requerimiento 4: Cierre de Caja

**User Story:** Como cajero, quiero cerrar la caja al final de mi turno con el monto real para realizar el arqueo.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/cash-registers/{id}/close con campo: closingAmount
2. EL sistema DEBE calcular el expectedAmount como: openingAmount + sum(DEPOSIT) + sum(SALE_INCOME) - sum(WITHDRAWAL)
3. EL sistema DEBE calcular la difference como: closingAmount - expectedAmount
4. EL sistema DEBE cambiar el status a 'CLOSED' y registrar closedAt
5. EL Sales_Service DEBE exigir permiso CASH_REGISTERS_CLOSE para cerrar una caja
6. EL sistema NO debe permitir cerrar una caja que ya está cerrada

### Requerimiento 5: Consulta de Cajas

**User Story:** Como gerente, quiero consultar las cajas registradoras para supervisar las operaciones diarias.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/cash-registers para listar cajas paginado con filtros: branchId, status, userId, dateFrom, dateTo
2. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/cash-registers/{id} para detalle de una caja con sus movimientos
3. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/cash-registers/my-open para obtener la caja abierta del usuario actual
4. EL Sales_Service DEBE exigir permiso CASH_REGISTERS_READ para consulta general, pero /my-open solo requiere autenticación

### Requerimiento 6: Apertura de Cuenta/Comanda

**User Story:** Como mesero, quiero abrir una cuenta (tab) para un cliente cuando se sienta en una mesa y va pidiendo productos.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/accounts para abrir una cuenta con campos: branchId, customerName, customerLastName, tableNumber, internalCode, notes
2. EL estado inicial DEBE ser 'OPEN'
3. EL sistema DEBE registrar openedBy automáticamente del JWT
4. EL Sales_Service DEBE exigir permiso ACCOUNTS_CREATE para abrir cuentas

### Requerimiento 7: Agregar Productos a Cuenta

**User Story:** Como mesero, quiero agregar productos a la cuenta del cliente conforme va pidiendo.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/accounts/{id}/details para agregar productos con campos: productId, quantity, unitPrice
2. EL sistema DEBE calcular el subtotal como quantity * unitPrice
3. EL sistema DEBE validar que la cuenta esté en estado OPEN
4. EL sistema DEBE registrar addedBy automáticamente del JWT
5. EL Sales_Service DEBE exigir permiso ACCOUNTS_ADD_ITEMS para agregar productos a cuentas

### Requerimiento 8: Cancelar Ítems de Cuenta

**User Story:** Como mesero/cajero, quiero cancelar ítems de una cuenta si el cliente cambia de opinión antes de pagar.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint PATCH /api/sales/v1/accounts/{accountId}/details/{detailId}/cancel para marcar un ítem como cancelado
2. EL sistema DEBE validar que la cuenta esté OPEN y el ítem no esté ya cancelado
3. EL campo isCancelled DEBE cambiar a true
4. EL Sales_Service DEBE exigir permiso ACCOUNTS_CANCEL_ITEMS

### Requerimiento 9: Consulta de Cuentas

**User Story:** Como cajero/gerente, quiero ver las cuentas abiertas y su detalle para gestionar el servicio.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/accounts para listar cuentas paginado con filtros: branchId, status, dateFrom, dateTo
2. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/accounts/{id} para detalle de una cuenta con sus ítems (no cancelados y cancelados)
3. EL detalle DEBE incluir el total calculado (sum de subtotales de ítems no cancelados)
4. EL Sales_Service DEBE exigir permiso ACCOUNTS_READ para consultar cuentas

### Requerimiento 10: Cierre de Cuenta y Generación de Venta

**User Story:** Como cajero, quiero cerrar una cuenta y generar la venta/factura correspondiente para cobrar al cliente.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/accounts/{id}/close con campos: customerId (opcional), cashRegisterId, paymentMethod, discountAmount (opcional)
2. EL sistema DEBE crear un registro en la tabla sales con los totales calculados
3. EL sistema DEBE copiar los detalles no cancelados de la cuenta a sale_details
4. EL sistema DEBE generar un sale_number único por sucursal (formato: VTA-YYYYMMDD-XXXX secuencial)
5. EL sistema DEBE cambiar el status de la cuenta a 'CLOSED' y registrar closedAt/closedBy
6. EL sistema DEBE crear un movimiento SALE_INCOME en la caja registradora por el total de la venta
7. EL sistema DEBE llamar al Inventory Service para deducir stock de cada producto vendido
8. SI la deducción de stock falla, la venta DEBE completarse igual pero registrar un log de advertencia
9. EL Sales_Service DEBE exigir permiso SALES_CREATE para cerrar cuentas y crear ventas

### Requerimiento 11: Venta Directa (sin cuenta previa)

**User Story:** Como cajero, quiero registrar una venta directa para clientes que piden y pagan inmediatamente sin abrir cuenta.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/sales con campos: branchId, cashRegisterId, customerId, paymentMethod, discountAmount, items[{productId, quantity, unitPrice}]
2. EL sistema DEBE crear la venta con todos los detalles en una sola transacción
3. EL sistema DEBE generar el sale_number secuencial
4. EL sistema DEBE crear el movimiento SALE_INCOME en la caja
5. EL sistema DEBE deducir stock via Inventory Service
6. EL Sales_Service DEBE exigir permiso SALES_CREATE

### Requerimiento 12: Consulta de Ventas

**User Story:** Como gerente, quiero consultar las ventas realizadas para control y seguimiento.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/sales para listar ventas paginado con filtros: branchId, status, dateFrom, dateTo, customerId, paymentMethod
2. EL Sales_Service DEBE exponer un endpoint GET /api/sales/v1/sales/{id} para detalle de una venta con sus ítems
3. EL Sales_Service DEBE exigir permiso SALES_READ

### Requerimiento 13: Cancelación de Venta

**User Story:** Como gerente, quiero cancelar una venta si fue registrada por error.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/sales/{id}/cancel con campo: reason (motivo de cancelación)
2. EL sistema DEBE cambiar el status a 'CANCELLED'
3. EL sistema DEBE revertir el stock via Inventory Service (agregar las cantidades de vuelta)
4. EL sistema NO debe cancelar ventas que ya están canceladas
5. EL Sales_Service DEBE exigir permiso SALES_CANCEL

### Requerimiento 14: Cancelación de Cuenta

**User Story:** Como cajero/gerente, quiero cancelar una cuenta completa si fue abierta por error.

#### Criterios de Aceptación

1. EL Sales_Service DEBE exponer un endpoint POST /api/sales/v1/accounts/{id}/cancel
2. EL sistema DEBE cambiar el status a 'CANCELLED'
3. EL sistema NO debe permitir cancelar cuentas que ya están cerradas o canceladas
4. EL Sales_Service DEBE exigir permiso ACCOUNTS_CANCEL

### Requerimiento 15: Integración con Inventory Service

**User Story:** Como sistema, necesito comunicarme con el Inventory Service para deducir/agregar stock al confirmar/cancelar ventas.

#### Criterios de Aceptación

1. EL Sales_Service DEBE llamar a POST {INVENTORY_SERVICE_URL}/api/inventory/v1/stock/deduct con los productos vendidos
2. EL Sales_Service DEBE llamar a POST {INVENTORY_SERVICE_URL}/api/inventory/v1/stock/add al cancelar una venta
3. SI el Inventory Service no está disponible, la venta DEBE completarse y el error se registra como advertencia
4. EL Sales_Service DEBE usar timeout de 30s y máximo 2 reintentos con backoff de 500ms
5. EL Sales_Service DEBE propagar el JWT del usuario para autenticación inter-servicio

### Requerimiento 16: Auditoría

**User Story:** Como administrador, quiero que todas las operaciones de escritura del Sales Service se registren para auditoría.

#### Criterios de Aceptación

1. EL Sales_Service DEBE publicar AuditEvent (de drinks-common) para CREATE, UPDATE, DELETE de todas las entidades
2. Los eventos DEBEN incluir: userId, username, action, module=SALES, entityName, entityId, description
3. Los eventos DEBEN procesarse de forma asíncrona después del commit de la transacción
