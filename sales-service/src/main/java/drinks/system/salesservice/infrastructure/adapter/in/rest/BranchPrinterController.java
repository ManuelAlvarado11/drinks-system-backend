package drinks.system.salesservice.infrastructure.adapter.in.rest;

import drinks.system.salesservice.application.dto.request.BranchPrinterRequest;
import drinks.system.salesservice.application.dto.response.BranchPrinterResponse;
import drinks.system.salesservice.domain.port.in.BranchPrinterUseCase;
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

@RestController
@RequestMapping("/api/sales/v1/branch-printers")
@RequiredArgsConstructor
public class BranchPrinterController {

    private final BranchPrinterUseCase useCase;

    @GetMapping
    @RequiresPermission("SALES_READ")
    public ResponseEntity<ApiResponse<List<BranchPrinterResponse>>> findByBranch(
            @RequestParam Long branchId) {
        return ResponseEntity.ok(ApiResponse.success(useCase.findByBranch(branchId)));
    }

    @PostMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<BranchPrinterResponse>> create(
            @Valid @RequestBody BranchPrinterRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(useCase.create(req, p.userId())));
    }

    @PutMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<BranchPrinterResponse>> update(
            @PathVariable Long id, @Valid @RequestBody BranchPrinterRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.ok(ApiResponse.success(useCase.update(id, req, p.userId())));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        useCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Impresora eliminada"));
    }

    /** Sends a small test ticket to verify the printer is reachable. */
    @PostMapping("/{id}/test")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<Void>> test(@PathVariable Long id) {
        useCase.test(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Ticket de prueba enviado"));
    }
}
