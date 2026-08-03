package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.application.mapper.PermissionMapper;
import drinks.system.accessservice.domain.model.Permission;
import drinks.system.accessservice.domain.port.out.PermissionRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAllByIsActiveTrue().stream()
                .map(permissionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return permissionJpaRepository.findByRoleIds(roleIds).stream()
                .map(permissionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findByIds(List<Long> ids) {
        return permissionJpaRepository.findAllByIdIn(ids).stream()
                .map(permissionMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionJpaRepository.findById(id).map(permissionMapper::toDomain);
    }
}
