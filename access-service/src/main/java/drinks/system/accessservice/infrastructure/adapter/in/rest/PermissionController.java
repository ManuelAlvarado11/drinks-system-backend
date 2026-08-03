package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.response.PermissionResponse;
import drinks.system.accessservice.application.dto.response.PermissionsByModuleResponse;
import drinks.system.accessservice.domain.port.in.PermissionUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.security.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/access/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionUseCase permissionUseCase;

    @GetMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> findAll() {
        List<PermissionResponse> response = permissionUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/modules")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<List<PermissionsByModuleResponse>>> findGroupedByModule() {
        List<PermissionsByModuleResponse> response = permissionUseCase.findGroupedByModule();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
