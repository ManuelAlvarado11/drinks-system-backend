package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.request.AssignPermissionsRequest;
import drinks.system.accessservice.application.dto.request.CreateRoleRequest;
import drinks.system.accessservice.application.dto.request.UpdateRoleRequest;
import drinks.system.accessservice.application.dto.response.RoleDetailResponse;
import drinks.system.accessservice.application.dto.response.RoleResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada para operaciones de gestión de roles.
 * Define los casos de uso CRUD y asignación de permisos a roles.
 */
public interface RoleUseCase {

    RoleDetailResponse create(CreateRoleRequest request);

    PageResponse<RoleResponse> findAll(Pageable pageable);

    RoleDetailResponse findById(Long id);

    RoleDetailResponse update(Long id, UpdateRoleRequest request);

    void delete(Long id);

    void assignPermissions(Long roleId, AssignPermissionsRequest request);
}
