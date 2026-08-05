package drinks.system.inventoryservice.infrastructure.adapter.in.rest;

import drinks.system.inventoryservice.application.dto.request.CreateSupplierRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateSupplierRequest;
import drinks.system.inventoryservice.application.dto.response.SupplierResponse;
import drinks.system.inventoryservice.domain.port.in.SupplierUseCase;
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

@RestController @RequestMapping("/api/inventory/v1/suppliers") @RequiredArgsConstructor
public class SupplierController {
    private final SupplierUseCase supplierUseCase;

    @PostMapping @RequiresPermission("INVENTORY_SUPPLIERS")
    public ResponseEntity<ApiResponse<SupplierResponse>> create(@Valid @RequestBody CreateSupplierRequest req, @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(supplierUseCase.create(req, p.userId())));
    }
    @GetMapping @RequiresPermission("INVENTORY_SUPPLIERS")
    public ResponseEntity<ApiResponse<PageResponse<SupplierResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("name"));
        return ResponseEntity.ok(ApiResponse.success(supplierUseCase.findAll(pageable, search)));
    }
    @GetMapping("/{id}") @RequiresPermission("INVENTORY_SUPPLIERS")
    public ResponseEntity<ApiResponse<SupplierResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(supplierUseCase.findById(id)));
    }
    @PutMapping("/{id}") @RequiresPermission("INVENTORY_SUPPLIERS")
    public ResponseEntity<ApiResponse<SupplierResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateSupplierRequest req, @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.ok(ApiResponse.success(supplierUseCase.update(id, req, p.userId())));
    }
    @DeleteMapping("/{id}") @RequiresPermission("INVENTORY_SUPPLIERS")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        supplierUseCase.delete(id); return ResponseEntity.ok(ApiResponse.success(null, "Proveedor desactivado"));
    }
}
