package drinks.system.salesservice.infrastructure.adapter.in.rest;

import drinks.system.salesservice.application.dto.request.CreateCustomerRequest;
import drinks.system.salesservice.application.dto.request.UpdateCustomerRequest;
import drinks.system.salesservice.application.dto.response.CustomerResponse;
import drinks.system.salesservice.domain.port.in.CustomerUseCase;
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

@RestController
@RequestMapping("/api/sales/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerUseCase customerUseCase;

    @PostMapping
    @RequiresPermission("SALES_CUSTOMERS")
    public ResponseEntity<ApiResponse<CustomerResponse>> create(
            @Valid @RequestBody CreateCustomerRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(customerUseCase.create(req, p.userId())));
    }

    @GetMapping
    @RequiresPermission("SALES_CUSTOMERS")
    public ResponseEntity<ApiResponse<PageResponse<CustomerResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("firstName"));
        return ResponseEntity.ok(ApiResponse.success(customerUseCase.findAll(pageable, search)));
    }

    @GetMapping("/{id}")
    @RequiresPermission("SALES_CUSTOMERS")
    public ResponseEntity<ApiResponse<CustomerResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(customerUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    @RequiresPermission("SALES_CUSTOMERS")
    public ResponseEntity<ApiResponse<CustomerResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest req,
            @AuthenticationPrincipal UserPrincipal p) {
        return ResponseEntity.ok(ApiResponse.success(customerUseCase.update(id, req, p.userId())));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("SALES_CUSTOMERS")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        customerUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cliente desactivado"));
    }
}
