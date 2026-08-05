# Documento de Requerimientos: Inventory Service

## Introducción

El Inventory Service gestiona productos, categorías, stock, movimientos de inventario, proveedores y órdenes de compra. Opera sobre el esquema `inventory` en el puerto 8083. Expone endpoints para que el Sales Service deduzca/agregue stock y para que los usuarios gestionen el inventario completo.

## Requerimientos

### Requerimiento 1: Gestión de Categorías
CRUD de categorías jerárquicas (parentCategoryId). Soft-delete. Permiso: INVENTORY_CATEGORIES.

### Requerimiento 2: Gestión de Productos
CRUD de productos con código único, precios (costo/venta), categoría, tamaño. Soft-delete. Permiso: INVENTORY_PRODUCTS.

### Requerimiento 3: Gestión de Stock por Sucursal
Consultar stock por producto/sucursal. Configurar stock mínimo. Alertar cuando stock <= mínimo. Permiso: INVENTORY_STOCK.

### Requerimiento 4: Deducción/Adición de Stock (Inter-servicio)
POST /api/inventory/v1/stock/deduct y /stock/add para uso del Sales Service. Crea movimientos automáticos tipo SALE/ENTRY. Autenticado por JWT.

### Requerimiento 5: Movimientos de Inventario
Registrar movimientos manuales (ENTRY, EXIT, ADJUSTMENT). Consultar historial filtrado. Permiso: INVENTORY_MOVEMENTS.

### Requerimiento 6: Gestión de Proveedores
CRUD de proveedores. Soft-delete. Permiso: INVENTORY_SUPPLIERS.

### Requerimiento 7: Órdenes de Compra
Crear órdenes con detalles (productos, cantidades, costos). Recibir mercadería (parcial o total). Cancelar órdenes. Al recibir, actualiza stock automáticamente. Permiso: INVENTORY_PURCHASES.

### Requerimiento 8: Auditoría
Publicar AuditEvent para todas las operaciones de escritura. Módulo=INVENTORY.
