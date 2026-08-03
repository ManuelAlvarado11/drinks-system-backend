package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.request.AdminChangePasswordRequest;
import drinks.system.accessservice.application.dto.request.AssignBranchesRequest;
import drinks.system.accessservice.application.dto.request.AssignRolesRequest;
import drinks.system.accessservice.application.dto.request.ChangeOwnPasswordRequest;
import drinks.system.accessservice.application.dto.request.CreateUserRequest;
import drinks.system.accessservice.application.dto.request.UpdateUserRequest;
import drinks.system.accessservice.application.dto.response.UserDetailResponse;
import drinks.system.accessservice.application.dto.response.UserProfileResponse;
import drinks.system.accessservice.application.dto.response.UserResponse;
import drinks.system.accessservice.domain.port.in.UserUseCase;
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
@RequestMapping("/api/access/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping
    @RequiresPermission("USERS_CREATE")
    public ResponseEntity<ApiResponse<UserDetailResponse>> create(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserDetailResponse response = userUseCase.create(request, principal.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @RequiresPermission("USERS_READ")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, sort));
        PageResponse<UserResponse> response = userUseCase.findAll(pageable, isActive, branchId, search);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @RequiresPermission("USERS_READ")
    public ResponseEntity<ApiResponse<UserDetailResponse>> findById(@PathVariable Long id) {
        UserDetailResponse response = userUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @RequiresPermission("USERS_UPDATE")
    public ResponseEntity<ApiResponse<UserDetailResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserDetailResponse response = userUseCase.update(id, request, principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("USERS_DELETE")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        userUseCase.delete(id, principal.userId());
        return ResponseEntity.ok(ApiResponse.success(null, "Usuario desactivado exitosamente"));
    }

    @PostMapping("/{id}/roles")
    @RequiresPermission("USERS_UPDATE")
    public ResponseEntity<ApiResponse<Void>> assignRoles(
            @PathVariable Long id,
            @Valid @RequestBody AssignRolesRequest request) {

        userUseCase.assignRoles(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Roles asignados exitosamente"));
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    @RequiresPermission("USERS_UPDATE")
    public ResponseEntity<ApiResponse<Void>> removeRole(
            @PathVariable Long id,
            @PathVariable Long roleId) {

        userUseCase.removeRole(id, roleId);
        return ResponseEntity.ok(ApiResponse.success(null, "Rol removido exitosamente"));
    }

    @PostMapping("/{id}/branches")
    @RequiresPermission("USERS_UPDATE")
    public ResponseEntity<ApiResponse<Void>> assignBranches(
            @PathVariable Long id,
            @Valid @RequestBody AssignBranchesRequest request) {

        userUseCase.assignBranches(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Sucursales asignadas exitosamente"));
    }

    @DeleteMapping("/{id}/branches/{branchId}")
    @RequiresPermission("USERS_UPDATE")
    public ResponseEntity<ApiResponse<Void>> removeBranch(
            @PathVariable Long id,
            @PathVariable Long branchId) {

        userUseCase.removeBranch(id, branchId);
        return ResponseEntity.ok(ApiResponse.success(null, "Sucursal removida exitosamente"));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changeOwnPassword(
            @Valid @RequestBody ChangeOwnPasswordRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        userUseCase.changeOwnPassword(principal.userId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Contraseña actualizada exitosamente"));
    }

    @PutMapping("/{id}/password")
    @RequiresPermission("USERS_UPDATE")
    public ResponseEntity<ApiResponse<Void>> adminChangePassword(
            @PathVariable Long id,
            @Valid @RequestBody AdminChangePasswordRequest request) {

        userUseCase.adminChangePassword(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Contraseña actualizada exitosamente"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserProfileResponse response = userUseCase.getProfile(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
