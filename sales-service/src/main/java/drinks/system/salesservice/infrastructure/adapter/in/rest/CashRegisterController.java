package drinks.system.salesservice.infrastructure.adapter.in.rest;

import drinks.system.salesservice.application.dto.request.CloseCashRegisterRequest;
import drinks.system.salesservice.application.dto.request.CreateMovementRequest;
import drinks.system.salesservice.application.dto.request.OpenCashRegisterRequest;
import drinks.system.salesservice.application.dto.response.CashRegisterDetailResponse;
import drinks.system.salesservice.application.dto.response.CashRegisterMovementResponse;
import drinks.system.salesservice.application.dto.response.CashRegisterResponse;
import drinks.system.salesservice.domain.port.in.CashRegisterUseCase;
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
import java.util.List;

@RestController
@RequestMapping("/api/sales/v1/cash-registers")
@RequiredArgsConstructor
public class CashRegisterController {
    private final CashRegisterUseCase cashRegisterUseCase;

    @PostMapping
    @RequiresPermission("CASH_REGISTERS_OPEN")
    public ResponseEntity<ApiResponse<CashRegisterResponse>> open(
            @Valid @RequestBody OpenCashRegisterRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cashRegisterUseCase.open(req, p.userId(), p.branchId())));
    }

    @GetMapping
    @RequiresPermission("CASH_REGISTERS_READ")
    public ResponseEntity<ApiResponse<PageResponse<CashRegisterResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "openedAt"));
        return ResponseEntity.ok(ApiResponse.success(
                cashRegisterUseCase.findAll(pageable, branchId, status, userId, dateFrom, dateTo)));
    }

    @GetMapping("/{id}")
    @RequiresPermission("CASH_REGISTERS_READ")
    public ResponseEntity<ApiResponse<CashRegisterDetailResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cashRegisterUseCase.findById(id)));
    }

    @GetMapping("/my-open")
    public ResponseEntity<ApiResponse<CashRegisterResponse>> findMyOpen(
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.ok(ApiResponse.success(cashRegisterUseCase.findMyOpen(p.userId())));
    }

    @PostMapping("/{id}/close")
    @RequiresPermission("CASH_REGISTERS_CLOSE")
    public ResponseEntity<ApiResponse<CashRegisterResponse>> close(
            @PathVariable Long id, @Valid @RequestBody CloseCashRegisterRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.ok(ApiResponse.success(cashRegisterUseCase.close(id, req, p.userId())));
    }

    @PostMapping("/{id}/movements")
    @RequiresPermission("CASH_REGISTERS_MOVEMENTS")
    public ResponseEntity<ApiResponse<CashRegisterMovementResponse>> addMovement(
            @PathVariable Long id, @Valid @RequestBody CreateMovementRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(cashRegisterUseCase.addMovement(id, req, p.userId())));
    }

    @GetMapping("/{id}/movements")
    @RequiresPermission("CASH_REGISTERS_READ")
    public ResponseEntity<ApiResponse<List<CashRegisterMovementResponse>>> findMovements(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(cashRegisterUseCase.findMovements(id)));
    }
}
