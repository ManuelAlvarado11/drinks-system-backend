package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.request.CreateMenuOptionRequest;
import drinks.system.accessservice.application.dto.request.UpdateMenuOptionRequest;
import drinks.system.accessservice.application.dto.response.MenuOptionResponse;
import drinks.system.accessservice.application.dto.response.MenuTreeResponse;
import drinks.system.accessservice.domain.port.in.MenuOptionUseCase;
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
@RequestMapping("/api/access/v1/menu-options")
@RequiredArgsConstructor
public class MenuOptionController {

    private final MenuOptionUseCase menuOptionUseCase;

    @PostMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<MenuOptionResponse>> create(
            @Valid @RequestBody CreateMenuOptionRequest request) {
        MenuOptionResponse response = menuOptionUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<List<MenuOptionResponse>>> findAll() {
        List<MenuOptionResponse> response = menuOptionUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<MenuOptionResponse>> findById(@PathVariable Long id) {
        MenuOptionResponse response = menuOptionUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<MenuOptionResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuOptionRequest request) {
        MenuOptionResponse response = menuOptionUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        menuOptionUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Opción de menú desactivada"));
    }

    @GetMapping("/my-menu")
    public ResponseEntity<ApiResponse<List<MenuTreeResponse>>> getMyMenu(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<MenuTreeResponse> response = menuOptionUseCase.getMyMenu(
                principal.userId(), principal.permissions());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
