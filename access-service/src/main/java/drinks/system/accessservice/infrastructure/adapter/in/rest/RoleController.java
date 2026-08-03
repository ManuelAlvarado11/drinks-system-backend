package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.request.AssignPermissionsRequest;
import drinks.system.accessservice.application.dto.request.CreateRoleRequest;
import drinks.system.accessservice.application.dto.request.UpdateRoleRequest;
import drinks.system.accessservice.application.dto.response.RoleDetailResponse;
import drinks.system.accessservice.application.dto.response.RoleResponse;
import drinks.system.accessservice.domain.port.in.RoleUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleUseCase roleUseCase;

    @PostMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<RoleDetailResponse>> create(
            @Valid @RequestBody CreateRoleRequest request) {

        RoleDetailResponse response = roleUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, sort));
        PageResponse<RoleResponse> response = roleUseCase.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<RoleDetailResponse>> findById(@PathVariable Long id) {
        RoleDetailResponse response = roleUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<RoleDetailResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request) {

        RoleDetailResponse response = roleUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        roleUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Rol desactivado exitosamente"));
    }

    @PutMapping("/{id}/permissions")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<Void>> assignPermissions(
            @PathVariable Long id,
            @Valid @RequestBody AssignPermissionsRequest request) {

        roleUseCase.assignPermissions(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Permisos actualizados exitosamente"));
    }
}
