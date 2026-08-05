package drinks.system.inventoryservice.infrastructure.adapter.in.rest;

import drinks.system.inventoryservice.application.dto.request.CreatePurchaseOrderRequest;
import drinks.system.inventoryservice.application.dto.request.ReceivePurchaseOrderRequest;
import drinks.system.inventoryservice.application.dto.response.PurchaseOrderDetailResponse;
import drinks.system.inventoryservice.application.dto.response.PurchaseOrderResponse;
import drinks.system.inventoryservice.domain.port.in.PurchaseOrderUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.security.RequiresPermission;
import drinks.system.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

@RestController @RequestMapping("/api/inventory/v1/purchase-orders") @RequiredArgsConstructor
public class PurchaseOrderController {
    private final PurchaseOrderUseCase purchaseOrderUseCase;

    @PostMapping @RequiresPermission("INVENTORY_PURCHASES")
    public ResponseEntity<ApiResponse<PurchaseOrderResponse>> create(@Valid @RequestBody CreatePurchaseOrderRequest req, @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(purchaseOrderUseCase.create(req, p.userId())));
    }
    @GetMapping @RequiresPermission("INVENTORY_PURCHASES")
    public ResponseEntity<ApiResponse<PageResponse<PurchaseOrderResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long supplierId, @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status, @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "orderDate"));
        return ResponseEntity.ok(ApiResponse.success(purchaseOrderUseCase.findAll(pageable, supplierId, branchId, status, dateFrom, dateTo)));
    }
    @GetMapping("/{id}") @RequiresPermission("INVENTORY_PURCHASES")
    public ResponseEntity<ApiResponse<PurchaseOrderDetailResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(purchaseOrderUseCase.findById(id)));
    }
    @PostMapping("/{id}/receive") @RequiresPermission("INVENTORY_PURCHASES")
    public ResponseEntity<ApiResponse<Void>> receive(@PathVariable Long id, @Valid @RequestBody ReceivePurchaseOrderRequest req, @AuthenticationPrincipal UserPrincipal p) {
        purchaseOrderUseCase.receive(id, req, p.userId());
        return ResponseEntity.ok(ApiResponse.success(null, "Mercadería recibida"));
    }
    @PostMapping("/{id}/cancel") @RequiresPermission("INVENTORY_PURCHASES")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal p) {
        purchaseOrderUseCase.cancel(id, p.userId());
        return ResponseEntity.ok(ApiResponse.success(null, "Orden cancelada"));
    }
}
