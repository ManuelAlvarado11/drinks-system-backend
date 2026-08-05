package drinks.system.inventoryservice.infrastructure.adapter.in.rest;

import drinks.system.inventoryservice.application.dto.request.CreateProductRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateProductRequest;
import drinks.system.inventoryservice.application.dto.response.ProductResponse;
import drinks.system.inventoryservice.domain.port.in.ProductUseCase;
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

@RestController @RequestMapping("/api/inventory/v1/products") @RequiredArgsConstructor
public class ProductController {
    private final ProductUseCase productUseCase;

    @PostMapping @RequiresPermission("INVENTORY_PRODUCTS")
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody CreateProductRequest req, @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productUseCase.create(req, p.userId())));
    }
    @GetMapping @RequiresPermission("INVENTORY_PRODUCTS")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoryId, @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("name"));
        return ResponseEntity.ok(ApiResponse.success(productUseCase.findAll(pageable, categoryId, isActive, search)));
    }
    @GetMapping("/{id}") @RequiresPermission("INVENTORY_PRODUCTS")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productUseCase.findById(id)));
    }
    @PutMapping("/{id}") @RequiresPermission("INVENTORY_PRODUCTS")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest req, @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.ok(ApiResponse.success(productUseCase.update(id, req, p.userId())));
    }
    @DeleteMapping("/{id}") @RequiresPermission("INVENTORY_PRODUCTS")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productUseCase.delete(id); return ResponseEntity.ok(ApiResponse.success(null, "Producto desactivado"));
    }
}
