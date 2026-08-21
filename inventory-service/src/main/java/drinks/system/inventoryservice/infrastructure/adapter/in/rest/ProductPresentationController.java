package drinks.system.inventoryservice.infrastructure.adapter.in.rest;

import drinks.system.inventoryservice.application.dto.request.CreateProductPresentationRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateProductPresentationRequest;
import drinks.system.inventoryservice.application.dto.response.ProductPresentationResponse;
import drinks.system.inventoryservice.domain.port.in.ProductPresentationUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/v1/products/{productId}/presentations")
@RequiredArgsConstructor
public class ProductPresentationController {

    private final ProductPresentationUseCase presentationUseCase;

    @GetMapping @RequiresPermission("INVENTORY_PRODUCTS")
    public ResponseEntity<ApiResponse<List<ProductPresentationResponse>>> findByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(presentationUseCase.findByProductId(productId)));
    }

    @PostMapping @RequiresPermission("INVENTORY_PRODUCTS")
    public ResponseEntity<ApiResponse<ProductPresentationResponse>> create(
            @PathVariable Long productId,
            @Valid @RequestBody CreateProductPresentationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(presentationUseCase.create(productId, request)));
    }

    @PutMapping("/{id}") @RequiresPermission("INVENTORY_PRODUCTS")
    public ResponseEntity<ApiResponse<ProductPresentationResponse>> update(
            @PathVariable Long productId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductPresentationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(presentationUseCase.update(id, request)));
    }

    @DeleteMapping("/{id}") @RequiresPermission("INVENTORY_PRODUCTS")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long productId, @PathVariable Long id) {
        presentationUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Presentación eliminada"));
    }
}
