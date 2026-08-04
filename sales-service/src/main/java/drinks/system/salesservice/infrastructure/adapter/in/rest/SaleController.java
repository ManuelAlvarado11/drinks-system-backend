package drinks.system.salesservice.infrastructure.adapter.in.rest;

import drinks.system.salesservice.application.dto.request.CancelSaleRequest;
import drinks.system.salesservice.application.dto.request.CreateDirectSaleRequest;
import drinks.system.salesservice.application.dto.response.SaleDetailResponse;
import drinks.system.salesservice.application.dto.response.SaleResponse;
import drinks.system.salesservice.domain.port.in.SaleUseCase;
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

@RestController
@RequestMapping("/api/sales/v1/sales")
@RequiredArgsConstructor
public class SaleController {
    private final SaleUseCase saleUseCase;

    @PostMapping
    @RequiresPermission("SALES_CREATE")
    public ResponseEntity<ApiResponse<SaleResponse>> createDirect(
            @Valid @RequestBody CreateDirectSaleRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(saleUseCase.createDirect(req, p.userId())));
    }

    @GetMapping
    @RequiresPermission("SALES_READ")
    public ResponseEntity<ApiResponse<PageResponse<SaleResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String paymentMethod) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "saleDate"));
        return ResponseEntity.ok(ApiResponse.success(
                saleUseCase.findAll(pageable, branchId, status, dateFrom, dateTo, customerId, paymentMethod)));
    }

    @GetMapping("/{id}")
    @RequiresPermission("SALES_READ")
    public ResponseEntity<ApiResponse<SaleDetailResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(saleUseCase.findById(id)));
    }

    @PostMapping("/{id}/cancel")
    @RequiresPermission("SALES_CANCEL")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable Long id, @Valid @RequestBody CancelSaleRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        saleUseCase.cancel(id, req, p.userId());
        return ResponseEntity.ok(ApiResponse.success(null, "Venta cancelada"));
    }
}
