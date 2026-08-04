# Documento de Diseño: Sales Service

## Arquitectura

El Sales Service sigue arquitectura hexagonal idéntica al Access Service:

```
sales-service/
├── src/main/java/drinks/system/salesservice/
│   ├── SalesServiceApplication.java
│   ├── config/                          # AsyncConfig
│   ├── domain/
│   │   ├── model/                       # Records inmutables
│   │   └── port/
│   │       ├── in/                      # Use Case interfaces
│   │       └── out/                     # Repository Port interfaces
│   ├── application/
│   │   ├── dto/
│   │   │   ├── request/                 # Request DTOs con Bean Validation
│   │   │   └── response/               # Response DTOs (records)
│   │   ├── mapper/                      # Mappers Entity ↔ Domain ↔ Response
│   │   └── service/                     # Implementaciones de Use Cases
│   └── infrastructure/
│       ├── adapter/
│       │   ├── in/rest/                 # Controllers REST
│       │   └── out/
│       │       ├── persistence/         # JPA Entities, Repositories, Adapters
│       │       └── client/              # REST client para Inventory Service
│       └── config/                      # SecurityConfig, OpenApiConfig, WebConfig
```

## Modelos de Dominio

### Customer
```java
public record Customer(
    Long id, String firstName, String lastName, String nitCi,
    String phone, String email, Boolean isActive,
    Instant deletedAt, Instant createdAt, Instant updatedAt,
    Long createdBy, Long updatedBy
) {}
```

### CashRegister
```java
public record CashRegister(
    Long id, Long branchId, Long userId,
    BigDecimal openingAmount, BigDecimal closingAmount,
    BigDecimal expectedAmount, BigDecimal difference,
    String status, Instant openedAt, Instant closedAt,
    String notes, Instant createdAt, Instant updatedAt,
    Long createdBy, Long updatedBy
) {}
```

### CashRegisterMovement
```java
public record CashRegisterMovement(
    Long id, Long cashRegisterId, String movementType,
    BigDecimal amount, String description,
    Instant createdAt, Long createdBy
) {}
```

### Account
```java
public record Account(
    Long id, Long branchId, String customerName, String customerLastName,
    String tableNumber, String internalCode, String status,
    Instant openedAt, Instant closedAt,
    Long openedBy, Long closedBy, String notes,
    Instant createdAt, Instant updatedAt,
    List<AccountDetail> details
) {}
```

### AccountDetail
```java
public record AccountDetail(
    Long id, Long accountId, Long productId,
    Integer quantity, BigDecimal unitPrice, BigDecimal subtotal,
    Instant addedAt, Long addedBy, Boolean isCancelled
) {}
```

### Sale
```java
public record Sale(
    Long id, Long branchId, Long accountId, Long customerId,
    Long cashRegisterId, String saleNumber,
    BigDecimal subtotal, BigDecimal discountAmount,
    BigDecimal taxAmount, BigDecimal totalAmount,
    String paymentMethod, String status, Instant saleDate,
    Instant createdAt, Instant updatedAt,
    Long createdBy, Long updatedBy,
    List<SaleDetail> details
) {}
```

### SaleDetail
```java
public record SaleDetail(
    Long id, Long saleId, Long productId,
    Integer quantity, BigDecimal unitPrice,
    BigDecimal subtotal, BigDecimal discount
) {}
```

## Puertos de Entrada (Use Cases)

### CustomerUseCase
- create(CreateCustomerRequest, Long userId): CustomerResponse
- findAll(Pageable, String search): PageResponse<CustomerResponse>
- findById(Long id): CustomerResponse
- update(Long id, UpdateCustomerRequest, Long userId): CustomerResponse
- delete(Long id): void

### CashRegisterUseCase
- open(OpenCashRegisterRequest, Long userId, Long branchId): CashRegisterResponse
- findAll(Pageable, Long branchId, String status, Long userId, Instant dateFrom, Instant dateTo): PageResponse<CashRegisterResponse>
- findById(Long id): CashRegisterDetailResponse
- findMyOpen(Long userId): CashRegisterResponse
- close(Long id, CloseCashRegisterRequest, Long userId): CashRegisterResponse
- addMovement(Long id, CreateMovementRequest, Long userId): CashRegisterMovementResponse
- findMovements(Long id): List<CashRegisterMovementResponse>

### AccountUseCase
- open(OpenAccountRequest, Long userId): AccountResponse
- findAll(Pageable, Long branchId, String status, Instant dateFrom, Instant dateTo): PageResponse<AccountResponse>
- findById(Long id): AccountDetailResponse
- addItem(Long accountId, AddAccountItemRequest, Long userId): AccountItemResponse
- cancelItem(Long accountId, Long detailId): void
- close(Long accountId, CloseAccountRequest, Long userId): SaleResponse
- cancel(Long accountId): void

### SaleUseCase
- createDirect(CreateDirectSaleRequest, Long userId): SaleResponse
- findAll(Pageable, Long branchId, String status, Instant dateFrom, Instant dateTo, Long customerId, String paymentMethod): PageResponse<SaleResponse>
- findById(Long id): SaleDetailResponse
- cancel(Long id, CancelSaleRequest, Long userId): void

## Puertos de Salida (Repository Ports)

### CustomerRepositoryPort
- findById(Long): Optional<Customer>
- save(Customer): Customer
- findAll(Pageable, String search): Page<Customer>
- existsByNitCi(String): boolean

### CashRegisterRepositoryPort
- findById(Long): Optional<CashRegister>
- save(CashRegister): CashRegister
- findAll(Pageable, Long branchId, String status, Long userId, Instant dateFrom, Instant dateTo): Page<CashRegister>
- findOpenByUserIdAndBranchId(Long userId, Long branchId): Optional<CashRegister>
- findOpenByUserId(Long userId): Optional<CashRegister>

### CashRegisterMovementRepositoryPort
- save(CashRegisterMovement): CashRegisterMovement
- findByCashRegisterId(Long): List<CashRegisterMovement>
- sumByTypeAndCashRegisterId(Long cashRegisterId, String type): BigDecimal

### AccountRepositoryPort
- findById(Long): Optional<Account>
- save(Account): Account
- findAll(Pageable, Long branchId, String status, Instant dateFrom, Instant dateTo): Page<Account>

### AccountDetailRepositoryPort
- save(AccountDetail): AccountDetail
- findByAccountId(Long): List<AccountDetail>
- findById(Long): Optional<AccountDetail>

### SaleRepositoryPort
- findById(Long): Optional<Sale>
- save(Sale): Sale
- findAll(Pageable, Long branchId, String status, Instant dateFrom, Instant dateTo, Long customerId, String paymentMethod): Page<Sale>
- generateSaleNumber(Long branchId): String

### SaleDetailRepositoryPort
- saveAll(List<SaleDetail>): List<SaleDetail>
- findBySaleId(Long): List<SaleDetail>

## Cliente de Inventory Service

### InventoryClient (Puerto de salida)
```java
public interface InventoryClient {
    void deductStock(List<StockDeductionItem> items, Long branchId);
    void addStock(List<StockDeductionItem> items, Long branchId);
}

public record StockDeductionItem(Long productId, Integer quantity) {}
```

La implementación usa RestClient de Spring con:
- Timeout: 30s
- Reintentos: 2 con backoff 500ms
- Propagación de JWT via JwtTokenHolder
- Si falla, log.warn() sin propagar excepción (la venta se completa)

## Endpoints REST

| Método | Ruta | Permiso | Descripción |
|--------|------|---------|-------------|
| POST | /api/sales/v1/customers | SALES_CUSTOMERS | Crear cliente |
| GET | /api/sales/v1/customers | SALES_CUSTOMERS | Listar clientes |
| GET | /api/sales/v1/customers/{id} | SALES_CUSTOMERS | Detalle cliente |
| PUT | /api/sales/v1/customers/{id} | SALES_CUSTOMERS | Actualizar cliente |
| DELETE | /api/sales/v1/customers/{id} | SALES_CUSTOMERS | Soft-delete cliente |
| POST | /api/sales/v1/cash-registers | CASH_REGISTERS_OPEN | Abrir caja |
| GET | /api/sales/v1/cash-registers | CASH_REGISTERS_READ | Listar cajas |
| GET | /api/sales/v1/cash-registers/{id} | CASH_REGISTERS_READ | Detalle caja |
| GET | /api/sales/v1/cash-registers/my-open | (autenticado) | Mi caja abierta |
| POST | /api/sales/v1/cash-registers/{id}/close | CASH_REGISTERS_CLOSE | Cerrar caja |
| POST | /api/sales/v1/cash-registers/{id}/movements | CASH_REGISTERS_MOVEMENTS | Registrar movimiento |
| GET | /api/sales/v1/cash-registers/{id}/movements | CASH_REGISTERS_READ | Listar movimientos |
| POST | /api/sales/v1/accounts | ACCOUNTS_CREATE | Abrir cuenta |
| GET | /api/sales/v1/accounts | ACCOUNTS_READ | Listar cuentas |
| GET | /api/sales/v1/accounts/{id} | ACCOUNTS_READ | Detalle cuenta |
| POST | /api/sales/v1/accounts/{id}/details | ACCOUNTS_ADD_ITEMS | Agregar producto |
| PATCH | /api/sales/v1/accounts/{aid}/details/{did}/cancel | ACCOUNTS_CANCEL_ITEMS | Cancelar ítem |
| POST | /api/sales/v1/accounts/{id}/close | SALES_CREATE | Cerrar cuenta → venta |
| POST | /api/sales/v1/accounts/{id}/cancel | ACCOUNTS_CANCEL | Cancelar cuenta |
| POST | /api/sales/v1/sales | SALES_CREATE | Venta directa |
| GET | /api/sales/v1/sales | SALES_READ | Listar ventas |
| GET | /api/sales/v1/sales/{id} | SALES_READ | Detalle venta |
| POST | /api/sales/v1/sales/{id}/cancel | SALES_CANCEL | Cancelar venta |

## Lógica de Generación de Número de Venta

Formato: `VTA-YYYYMMDD-XXXX`
- YYYYMMDD: fecha actual
- XXXX: secuencial del día por sucursal (padded a 4 dígitos)
- Se obtiene consultando el máximo sale_number del día para la sucursal y sumando 1

## Flujo de Cierre de Cuenta

1. Validar que la cuenta esté OPEN
2. Calcular totales desde account_details no cancelados
3. Crear registro en sales con sale_number generado
4. Copiar account_details (no cancelados) a sale_details
5. Crear movimiento SALE_INCOME en la caja registradora
6. Cambiar cuenta a CLOSED
7. Llamar a Inventory Service para deducir stock (best-effort)
8. Publicar AuditEvent

## Tecnologías y Dependencias

- Spring Boot 4.1 (starter-web, starter-data-jpa, starter-security, starter-validation)
- drinks-common (JwtAuthenticationFilter, JwtTokenProvider, UserPrincipal, @RequiresPermission, PermissionAspect, PageResponse, ApiResponse, AuditEvent, excepciones)
- PostgreSQL driver
- Lombok
- SpringDoc OpenAPI
