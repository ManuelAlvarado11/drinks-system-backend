package drinks.system.inventoryservice.infrastructure.adapter.in.rest;

import drinks.system.inventoryservice.application.dto.request.CreateCategoryRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateCategoryRequest;
import drinks.system.inventoryservice.application.dto.response.CategoryResponse;
import drinks.system.inventoryservice.domain.port.in.CategoryUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.security.RequiresPermission;
import drinks.system.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/inventory/v1/categories") @RequiredArgsConstructor
public class CategoryController {
    private final CategoryUseCase categoryUseCase;

    @PostMapping @RequiresPermission("INVENTORY_CATEGORIES")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CreateCategoryRequest req, @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(categoryUseCase.create(req, p.userId())));
    }
    @GetMapping @RequiresPermission("INVENTORY_CATEGORIES")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> findAll(@RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(ApiResponse.success(categoryUseCase.findAll(isActive)));
    }
    @GetMapping("/{id}") @RequiresPermission("INVENTORY_CATEGORIES")
    public ResponseEntity<ApiResponse<CategoryResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryUseCase.findById(id)));
    }
    @PutMapping("/{id}") @RequiresPermission("INVENTORY_CATEGORIES")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest req, @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.ok(ApiResponse.success(categoryUseCase.update(id, req, p.userId())));
    }
    @DeleteMapping("/{id}") @RequiresPermission("INVENTORY_CATEGORIES")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryUseCase.delete(id); return ResponseEntity.ok(ApiResponse.success(null, "Categoría desactivada"));
    }
}
