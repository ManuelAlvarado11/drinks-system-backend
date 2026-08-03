package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.request.BranchStatusRequest;
import drinks.system.accessservice.application.dto.request.CreateBranchRequest;
import drinks.system.accessservice.application.dto.request.UpdateBranchRequest;
import drinks.system.accessservice.application.dto.response.BranchResponse;
import drinks.system.accessservice.domain.port.in.BranchUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.security.RequiresPermission;
import drinks.system.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchUseCase branchUseCase;

    @PostMapping
    @RequiresPermission("BRANCHES_CREATE")
    public ResponseEntity<ApiResponse<BranchResponse>> create(
            @Valid @RequestBody CreateBranchRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        BranchResponse response = branchUseCase.create(request, principal.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @RequiresPermission("BRANCHES_READ")
    public ResponseEntity<ApiResponse<PageResponse<BranchResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, sort));
        PageResponse<BranchResponse> response = branchUseCase.findAll(pageable, isActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @RequiresPermission("BRANCHES_READ")
    public ResponseEntity<ApiResponse<BranchResponse>> findById(@PathVariable Long id) {
        BranchResponse response = branchUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @RequiresPermission("BRANCHES_UPDATE")
    public ResponseEntity<ApiResponse<BranchResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBranchRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        BranchResponse response = branchUseCase.update(id, request, principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission("BRANCHES_UPDATE")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody BranchStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        branchUseCase.updateStatus(id, request, principal.userId());
        return ResponseEntity.ok(ApiResponse.success(null, "Estado actualizado exitosamente"));
    }
}
