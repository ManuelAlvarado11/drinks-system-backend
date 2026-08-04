# Plan de Implementación: Sales Service

## Visión General

Implementación bottom-up del Sales Service siguiendo arquitectura hexagonal (misma estructura que Access Service): modelos de dominio → puertos → entidades JPA → repositorios → DTOs → mappers → adaptadores → servicios → configuración → controladores. El módulo de Cajas y Cuentas es prioritario por ser prerequisito de Ventas.

## Tareas

- [ ] 1. Modelos de dominio
  - [ ] 1.1 Crear modelos de dominio (records Java)
    - Crear 6 records en `domain/model/`: Customer, CashRegister, CashRegisterMovement, Account, AccountDetail, Sale, SaleDetail
    - Usar BigDecimal para montos, Instant para timestamps
    - _Requerimientos: 1-14_

- [ ] 2. Puertos de entrada y salida
  - [ ] 2.1 Crear puertos de entrada (Use Case interfaces)
    - Crear 4 interfaces en `domain/port/in/`: CustomerUseCase, CashRegisterUseCase, AccountUseCase, SaleUseCase
    - Definir firmas según diseño
    - _Requerimientos: 1-14_

  - [ ] 2.2 Crear puertos de salida (Repository Port interfaces)
    - Crear 7 interfaces en `domain/port/out/`: CustomerRepositoryPort, CashRegisterRepositoryPort, CashRegisterMovementRepositoryPort, AccountRepositoryPort, AccountDetailRepositoryPort, SaleRepositoryPort, SaleDetailRepositoryPort
    - Crear 1 interfaz cliente: InventoryClient
    - _Requerimientos: 1-15_

- [ ] 3. Entidades JPA
  - [ ] 3.1 Crear entidades JPA (CustomerEntity, CashRegisterEntity, CashRegisterMovementEntity, AccountEntity, AccountDetailEntity, SaleEntity, SaleDetailEntity)
    - Schema "sales", @PrePersist/@PreUpdate para timestamps
    - BigDecimal para campos numéricos monetarios
    - CHECK constraints mapeados via @Column + DDL ya existente
    - _Requerimientos: 1-14_

- [ ] 4. Repositorios Spring Data JPA
  - [ ] 4.1 Crear repositorios JPA con queries filtrados
    - CustomerJpaRepository: búsqueda por nombre/NIT, existsByNitCi
    - CashRegisterJpaRepository: filtrado por branch/status/user/fecha, findOpenByUserAndBranch
    - CashRegisterMovementJpaRepository: findByCashRegisterId, sumByType
    - AccountJpaRepository: filtrado por branch/status/fecha
    - AccountDetailJpaRepository: findByAccountId
    - SaleJpaRepository: filtrado completo, generateSaleNumber query
    - SaleDetailJpaRepository: findBySaleId
    - _Requerimientos: 1-14_

- [ ] 5. DTOs (Request y Response)
  - [ ] 5.1 Crear DTOs de request
    - CreateCustomerRequest, UpdateCustomerRequest
    - OpenCashRegisterRequest, CloseCashRegisterRequest, CreateMovementRequest
    - OpenAccountRequest, AddAccountItemRequest, CloseAccountRequest
    - CreateDirectSaleRequest, CancelSaleRequest
    - Bean Validation en todos
    - _Requerimientos: 1-14_

  - [ ] 5.2 Crear DTOs de response
    - CustomerResponse
    - CashRegisterResponse, CashRegisterDetailResponse, CashRegisterMovementResponse
    - AccountResponse, AccountDetailResponse, AccountItemResponse
    - SaleResponse, SaleDetailResponse
    - _Requerimientos: 1-14_

- [ ] 6. Mappers
  - [ ] 6.1 Crear mappers (CustomerMapper, CashRegisterMapper, AccountMapper, SaleMapper)
    - Entity ↔ Domain ↔ Response para cada entidad
    - _Requerimientos: 1-14_

- [ ] 7. Adaptadores de repositorio
  - [ ] 7.1 Crear adaptadores de repositorio para cada port
    - CustomerRepositoryAdapter, CashRegisterRepositoryAdapter, CashRegisterMovementRepositoryAdapter, AccountRepositoryAdapter, AccountDetailRepositoryAdapter, SaleRepositoryAdapter, SaleDetailRepositoryAdapter
    - _Requerimientos: 1-14_

- [ ] 8. Cliente de Inventory Service
  - [ ] 8.1 Implementar InventoryClientImpl
    - RestClient con timeout 30s, 2 reintentos, backoff 500ms
    - Propagación de JWT
    - Best-effort: log.warn en caso de fallo sin propagar excepción
    - _Requerimientos: 15_

- [ ] 9. Configuración adicional
  - [ ] 9.1 Crear AsyncConfig
    - @EnableAsync para procesamiento asíncrono de eventos de auditoría
    - _Requerimientos: 16_

- [ ] 10. Servicios de aplicación
  - [ ] 10.1 Implementar CustomerServiceImpl
    - CRUD completo con soft-delete y validación de duplicados NIT
    - Publicar AuditEvent
    - _Requerimientos: 1_

  - [ ] 10.2 Implementar CashRegisterServiceImpl
    - Apertura con validación de caja única abierta por usuario/sucursal
    - Cierre con cálculo de expectedAmount y difference
    - Movimientos (DEPOSIT, WITHDRAWAL) con validación de caja abierta
    - Publicar AuditEvent
    - _Requerimientos: 2, 3, 4, 5_

  - [ ] 10.3 Implementar AccountServiceImpl
    - Apertura, agregar ítems, cancelar ítems, cierre → venta, cancelar cuenta
    - Cierre genera sale_number, crea sale + sale_details, movimiento SALE_INCOME, deducción stock
    - Publicar AuditEvent
    - _Requerimientos: 6, 7, 8, 9, 10, 14_

  - [ ] 10.4 Implementar SaleServiceImpl
    - Venta directa (sin cuenta previa), consulta paginada, cancelación con reversión de stock
    - Publicar AuditEvent
    - _Requerimientos: 11, 12, 13_

  - [ ] 10.5 Implementar AuditEventListener
    - @Async + @TransactionalEventListener(AFTER_COMMIT)
    - Guardar en audit_logs via access-service o local
    - _Requerimientos: 16_

- [ ] 11. Controladores REST
  - [ ] 11.1 Implementar CustomerController
    - CRUD en /api/sales/v1/customers con @RequiresPermission("SALES_CUSTOMERS")
    - _Requerimientos: 1_

  - [ ] 11.2 Implementar CashRegisterController
    - Apertura, cierre, movimientos, consulta, my-open en /api/sales/v1/cash-registers
    - _Requerimientos: 2, 3, 4, 5_

  - [ ] 11.3 Implementar AccountController
    - CRUD + details + close + cancel en /api/sales/v1/accounts
    - _Requerimientos: 6, 7, 8, 9, 10, 14_

  - [ ] 11.4 Implementar SaleController
    - Venta directa + consulta + cancel en /api/sales/v1/sales
    - _Requerimientos: 11, 12, 13_

- [ ] 12. Verificar compilación
  - Asegurar que el servicio compila correctamente y arranca sin errores.

## Notas

- El scaffold del proyecto (pom.xml, SecurityConfig, OpenApiConfig, WebConfig, application.yml) ya existe
- Se reutilizan componentes de drinks-common: JwtTokenProvider, JwtAuthenticationFilter, UserPrincipal, @RequiresPermission, PermissionAspect, PageResponse, ApiResponse, AuditEvent, excepciones
- Las migraciones de BD ya existen en access-service (V15-V18, V27) y se ejecutan con Flyway desde ahí
- El sales-service NO ejecuta Flyway (no tiene spring.flyway config) — solo usa JPA validate
- La comunicación con Inventory Service es best-effort (la venta nunca falla por stock)

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["2.1", "2.2", "3.1"] },
    { "id": 2, "tasks": ["4.1", "5.1", "5.2"] },
    { "id": 3, "tasks": ["6.1"] },
    { "id": 4, "tasks": ["7.1", "8.1", "9.1"] },
    { "id": 5, "tasks": ["10.1", "10.2", "10.3", "10.4", "10.5"] },
    { "id": 6, "tasks": ["11.1", "11.2", "11.3", "11.4"] },
    { "id": 7, "tasks": ["12"] }
  ]
}
```
