package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.request.CreateSystemParameterRequest;
import drinks.system.accessservice.application.dto.request.UpdateSystemParameterRequest;
import drinks.system.accessservice.application.dto.response.SystemParameterResponse;
import drinks.system.accessservice.domain.port.in.SystemParameterUseCase;
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
@RequestMapping("/api/access/v1/system-parameters")
@RequiredArgsConstructor
public class SystemParameterController {

    private final SystemParameterUseCase systemParameterUseCase;

    @PostMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<SystemParameterResponse>> create(
            @Valid @RequestBody CreateSystemParameterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        SystemParameterResponse response = systemParameterUseCase.create(request, principal.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<PageResponse<SystemParameterResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "parameterKey") String sort) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.ASC, sort));
        PageResponse<SystemParameterResponse> response = systemParameterUseCase.findAll(pageable, module, isActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<SystemParameterResponse>> findById(@PathVariable Long id) {
        SystemParameterResponse response = systemParameterUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/key/{key}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<SystemParameterResponse>> findByKey(@PathVariable String key) {
        SystemParameterResponse response = systemParameterUseCase.findByKey(key);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<SystemParameterResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSystemParameterRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        SystemParameterResponse response = systemParameterUseCase.update(id, request, principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        systemParameterUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Parámetro desactivado exitosamente"));
    }
}
