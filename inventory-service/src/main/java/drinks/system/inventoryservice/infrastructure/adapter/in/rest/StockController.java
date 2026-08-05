package drinks.system.inventoryservice.infrastructure.adapter.in.rest;

import drinks.system.inventoryservice.application.dto.request.StockDeductRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateStockConfigRequest;
import drinks.system.inventoryservice.application.dto.response.ProductStockResponse;
import drinks.system.inventoryservice.domain.port.in.StockUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.security.RequiresPermission;
import drinks.system.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/inventory/v1/stock") @RequiredArgsConstructor
public class StockController {
    private final StockUseCase stockUseCase;

    @GetMapping @RequiresPermission("INVENTORY_STOCK")
    public ResponseEntity<ApiResponse<PageResponse<ProductStockResponse>>> findByBranch(
            @RequestParam Long branchId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) Boolean lowStock) {
        var pageable = PageRequest.of(page, Math.min(size, 100));
        return ResponseEntity.ok(ApiResponse.success(stockUseCase.findByBranch(pageable, branchId, lowStock)));
    }
    @GetMapping("/{productId}/branch/{branchId}") @RequiresPermission("INVENTORY_STOCK")
    public ResponseEntity<ApiResponse<ProductStockResponse>> findByProductAndBranch(@PathVariable Long productId, @PathVariable Long branchId) {
        return ResponseEntity.ok(ApiResponse.success(stockUseCase.findByProductAndBranch(productId, branchId)));
    }
    @PutMapping("/{productId}/branch/{branchId}/config") @RequiresPermission("INVENTORY_STOCK")
    public ResponseEntity<ApiResponse<Void>> updateConfig(@PathVariable Long productId, @PathVariable Long branchId, @Valid @RequestBody UpdateStockConfigRequest req) {
        stockUseCase.updateConfig(productId, branchId, req);
        return ResponseEntity.ok(ApiResponse.success(null, "Stock mínimo actualizado"));
    }
    @PostMapping("/deduct")
    public ResponseEntity<ApiResponse<Void>> deduct(@Valid @RequestBody StockDeductRequest req, @AuthenticationPrincipal UserPrincipal p) {
        stockUseCase.deductStock(req, p.userId());
        return ResponseEntity.ok(ApiResponse.success(null, "Stock deducido"));
    }
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> add(@Valid @RequestBody StockDeductRequest req, @AuthenticationPrincipal UserPrincipal p) {
        stockUseCase.addStock(req, p.userId());
        return ResponseEntity.ok(ApiResponse.success(null, "Stock agregado"));
    }
}
