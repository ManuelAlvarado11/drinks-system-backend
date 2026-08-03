package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.response.PermissionResponse;
import drinks.system.accessservice.application.dto.response.PermissionsByModuleResponse;
import drinks.system.accessservice.application.mapper.PermissionMapper;
import drinks.system.accessservice.domain.model.Permission;
import drinks.system.accessservice.domain.port.in.PermissionUseCase;
import drinks.system.accessservice.domain.port.out.PermissionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionUseCase {

    private final PermissionRepositoryPort permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> findAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionsByModuleResponse> findGroupedByModule() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissionMapper.toGroupedByModule(permissions);
    }
}
