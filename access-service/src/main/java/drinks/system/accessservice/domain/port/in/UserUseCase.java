package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.request.AdminChangePasswordRequest;
import drinks.system.accessservice.application.dto.request.AssignBranchesRequest;
import drinks.system.accessservice.application.dto.request.AssignRolesRequest;
import drinks.system.accessservice.application.dto.request.ChangeOwnPasswordRequest;
import drinks.system.accessservice.application.dto.request.CreateUserRequest;
import drinks.system.accessservice.application.dto.request.UpdateUserRequest;
import drinks.system.accessservice.application.dto.response.UserDetailResponse;
import drinks.system.accessservice.application.dto.response.UserProfileResponse;
import drinks.system.accessservice.application.dto.response.UserResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada para operaciones de gestión de usuarios.
 * Define los casos de uso CRUD, asignación de roles/sucursales,
 * cambio de contraseña y consulta de perfil.
 */
public interface UserUseCase {

    UserDetailResponse create(CreateUserRequest request, Long currentUserId);

    PageResponse<UserResponse> findAll(Pageable pageable, Boolean isActive, Long branchId, String search);

    UserDetailResponse findById(Long id);

    UserDetailResponse update(Long id, UpdateUserRequest request, Long currentUserId);

    void delete(Long id, Long currentUserId);

    void assignRoles(Long userId, AssignRolesRequest request);

    void removeRole(Long userId, Long roleId);

    void assignBranches(Long userId, AssignBranchesRequest request);

    void removeBranch(Long userId, Long branchId);

    void changeOwnPassword(Long userId, ChangeOwnPasswordRequest request);

    void adminChangePassword(Long userId, AdminChangePasswordRequest request);

    UserProfileResponse getProfile(Long userId);
}
