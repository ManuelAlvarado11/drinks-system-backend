package drinks.system.inventoryservice.infrastructure.adapter.in.rest;

import drinks.system.inventoryservice.application.dto.request.CreateMovementRequest;
import drinks.system.inventoryservice.application.dto.response.InventoryMovementResponse;
import drinks.system.inventoryservice.domain.port.in.InventoryMovementUseCase;
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

@RestController @RequestMapping("/api/inventory/v1/movements") @RequiredArgsConstructor
public class InventoryMovementController {
    private final InventoryMovementUseCase movementUseCase;

    @PostMapping @RequiresPermission("INVENTORY_MOVEMENTS")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> create(@Valid @RequestBody CreateMovementRequest req, @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(movementUseCase.create(req, p.userId())));
    }
    @GetMapping @RequiresPermission("INVENTORY_MOVEMENTS")
    public ResponseEntity<ApiResponse<PageResponse<InventoryMovementResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long productId, @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String type, @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.success(movementUseCase.findAll(pageable, productId, branchId, type, dateFrom, dateTo)));
    }
}
