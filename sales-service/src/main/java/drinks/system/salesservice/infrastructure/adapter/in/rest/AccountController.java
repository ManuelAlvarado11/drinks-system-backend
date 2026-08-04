package drinks.system.salesservice.infrastructure.adapter.in.rest;

import drinks.system.salesservice.application.dto.request.AddAccountItemRequest;
import drinks.system.salesservice.application.dto.request.CloseAccountRequest;
import drinks.system.salesservice.application.dto.request.OpenAccountRequest;
import drinks.system.salesservice.application.dto.response.*;
import drinks.system.salesservice.domain.port.in.AccountUseCase;
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
@RequestMapping("/api/sales/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountUseCase accountUseCase;

    @PostMapping
    @RequiresPermission("ACCOUNTS_CREATE")
    public ResponseEntity<ApiResponse<AccountResponse>> open(
            @Valid @RequestBody OpenAccountRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(accountUseCase.open(req, p.userId())));
    }

    @GetMapping
    @RequiresPermission("ACCOUNTS_READ")
    public ResponseEntity<ApiResponse<PageResponse<AccountResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "openedAt"));
        return ResponseEntity.ok(ApiResponse.success(accountUseCase.findAll(pageable, branchId, status, dateFrom, dateTo)));
    }

    @GetMapping("/{id}")
    @RequiresPermission("ACCOUNTS_READ")
    public ResponseEntity<ApiResponse<AccountDetailResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(accountUseCase.findById(id)));
    }

    @PostMapping("/{id}/details")
    @RequiresPermission("ACCOUNTS_ADD_ITEMS")
    public ResponseEntity<ApiResponse<AccountItemResponse>> addItem(
            @PathVariable Long id, @Valid @RequestBody AddAccountItemRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(accountUseCase.addItem(id, req, p.userId())));
    }

    @PatchMapping("/{accountId}/details/{detailId}/cancel")
    @RequiresPermission("ACCOUNTS_CANCEL_ITEMS")
    public ResponseEntity<ApiResponse<Void>> cancelItem(
            @PathVariable Long accountId, @PathVariable Long detailId) {
        accountUseCase.cancelItem(accountId, detailId);
        return ResponseEntity.ok(ApiResponse.success(null, "Ítem cancelado"));
    }

    @PostMapping("/{id}/close")
    @RequiresPermission("SALES_CREATE")
    public ResponseEntity<ApiResponse<SaleResponse>> close(
            @PathVariable Long id, @Valid @RequestBody CloseAccountRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.ok(ApiResponse.success(accountUseCase.close(id, req, p.userId())));
    }

    @PostMapping("/{id}/cancel")
    @RequiresPermission("ACCOUNTS_CANCEL")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long id) {
        accountUseCase.cancel(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cuenta cancelada"));
    }
}
