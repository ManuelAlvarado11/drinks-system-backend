package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.request.AssignPermissionsRequest;
import drinks.system.accessservice.application.dto.request.CreateRoleRequest;
import drinks.system.accessservice.application.dto.request.UpdateRoleRequest;
import drinks.system.accessservice.application.dto.response.PermissionResponse;
import drinks.system.accessservice.application.dto.response.RoleDetailResponse;
import drinks.system.accessservice.application.dto.response.RoleResponse;
import drinks.system.accessservice.application.mapper.PermissionMapper;
import drinks.system.accessservice.application.mapper.RoleMapper;
import drinks.system.accessservice.domain.model.Permission;
import drinks.system.accessservice.domain.model.Role;
import drinks.system.accessservice.domain.port.in.RoleUseCase;
import drinks.system.accessservice.domain.port.out.PermissionRepositoryPort;
import drinks.system.accessservice.domain.port.out.RoleRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RolePermissionEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.PermissionJpaRepository;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.RolePermissionJpaRepository;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.exception.BusinessConflictException;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleUseCase {

    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;
    private final RolePermissionJpaRepository rolePermissionJpaRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public RoleDetailResponse create(CreateRoleRequest request) {
        if (roleRepository.existsByCode(request.code())) {
            throw new BusinessConflictException("El código de rol ya existe: " + request.code());
        }

        Role role = new Role(
                null, request.code(), request.name(), request.description(),
                true, null, null, Collections.emptyList()
        );

        Role saved = roleRepository.save(role);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "CREATE", "ACCESS",
                "Role", saved.id(), null, null, null,
                "Rol creado: " + saved.code()
        ));

        return buildDetailResponse(saved.id());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> findAll(Pageable pageable) {
        Page<Role> page = roleRepository.findAll(pageable);
        List<RoleResponse> content = page.getContent().stream()
                .map(role -> {
                    RoleEntity entity = roleJpaRepository.findById(role.id()).orElseThrow();
                    int permCount = rolePermissionJpaRepository.findByRole(entity).size();
                    return roleMapper.toResponse(role, permCount, 0);
                })
                .toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDetailResponse findById(Long id) {
        return buildDetailResponse(id);
    }

    @Override
    @Transactional
    public RoleDetailResponse update(Long id, UpdateRoleRequest request) {
        RoleEntity entity = roleJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }
        roleJpaRepository.save(entity);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "UPDATE", "ACCESS",
                "Role", id, null, null, null,
                "Rol actualizado: " + entity.getCode()
        ));

        return buildDetailResponse(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RoleEntity entity = roleJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));

        entity.setIsActive(false);
        roleJpaRepository.save(entity);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "DELETE", "ACCESS",
                "Role", id, null, null, null,
                "Rol desactivado: " + entity.getCode()
        ));
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, AssignPermissionsRequest request) {
        RoleEntity role = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", roleId));

        // Remove all existing permissions
        rolePermissionJpaRepository.deleteAllByRole(role);

        // Assign new permissions
        for (Long permissionId : request.permissionIds()) {
            PermissionEntity permission = permissionJpaRepository.findById(permissionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Permiso", permissionId));
            RolePermissionEntity rp = new RolePermissionEntity();
            rp.setRole(role);
            rp.setPermission(permission);
            rolePermissionJpaRepository.save(rp);
        }

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "UPDATE", "ACCESS",
                "Role", roleId, null, null, null,
                "Permisos actualizados para rol: " + role.getCode()
        ));
    }

    private RoleDetailResponse buildDetailResponse(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", roleId));

        List<Long> roleIds = List.of(roleId);
        List<Permission> permissions = permissionRepository.findByRoleIds(roleIds);
        List<PermissionResponse> permissionResponses = permissions.stream()
                .map(permissionMapper::toResponse)
                .toList();

        return roleMapper.toDetailResponse(role, permissionResponses);
    }
}
