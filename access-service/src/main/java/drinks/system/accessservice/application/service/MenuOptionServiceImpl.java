package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.request.CreateMenuOptionRequest;
import drinks.system.accessservice.application.dto.request.UpdateMenuOptionRequest;
import drinks.system.accessservice.application.dto.response.MenuOptionResponse;
import drinks.system.accessservice.application.dto.response.MenuTreeResponse;
import drinks.system.accessservice.application.mapper.MenuOptionMapper;
import drinks.system.accessservice.domain.model.Permission;
import drinks.system.accessservice.domain.model.SystemMenuOption;
import drinks.system.accessservice.domain.port.in.MenuOptionUseCase;
import drinks.system.accessservice.domain.port.out.MenuOptionRepositoryPort;
import drinks.system.accessservice.domain.port.out.PermissionRepositoryPort;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuOptionServiceImpl implements MenuOptionUseCase {

    private final MenuOptionRepositoryPort menuOptionRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final MenuOptionMapper menuOptionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public MenuOptionResponse create(CreateMenuOptionRequest request) {
        SystemMenuOption option = new SystemMenuOption(
                null, request.name(), request.route(), request.icon(),
                request.parentId(), request.permissionId(), request.sortOrder(),
                true, null, null
        );

        SystemMenuOption saved = menuOptionRepository.save(option);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "CREATE", "ACCESS",
                "MenuOption", saved.id(), null, null, null,
                "Opción de menú creada: " + saved.name()
        ));

        return menuOptionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuOptionResponse> findAll() {
        return menuOptionRepository.findAll().stream()
                .map(menuOptionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MenuOptionResponse findById(Long id) {
        SystemMenuOption option = menuOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opción de menú", id));
        return menuOptionMapper.toResponse(option);
    }

    @Override
    @Transactional
    public MenuOptionResponse update(Long id, UpdateMenuOptionRequest request) {
        SystemMenuOption existing = menuOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opción de menú", id));

        SystemMenuOption updated = new SystemMenuOption(
                existing.id(),
                request.name() != null ? request.name() : existing.name(),
                request.route() != null ? request.route() : existing.route(),
                request.icon() != null ? request.icon() : existing.icon(),
                request.parentId() != null ? request.parentId() : existing.parentId(),
                request.permissionId() != null ? request.permissionId() : existing.permissionId(),
                request.sortOrder() != null ? request.sortOrder() : existing.sortOrder(),
                existing.isActive(),
                existing.createdAt(),
                existing.updatedAt()
        );

        SystemMenuOption saved = menuOptionRepository.save(updated);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "UPDATE", "ACCESS",
                "MenuOption", id, null, null, null,
                "Opción de menú actualizada: " + saved.name()
        ));

        return menuOptionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SystemMenuOption existing = menuOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opción de menú", id));

        SystemMenuOption deleted = new SystemMenuOption(
                existing.id(), existing.name(), existing.route(), existing.icon(),
                existing.parentId(), existing.permissionId(), existing.sortOrder(),
                false, existing.createdAt(), existing.updatedAt()
        );

        menuOptionRepository.save(deleted);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "DELETE", "ACCESS",
                "MenuOption", id, null, null, null,
                "Opción de menú desactivada: " + existing.name()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuTreeResponse> getMyMenu(Long userId, List<String> permissions) {
        // Get permission IDs from permission codes
        List<Permission> allPermissions = permissionRepository.findAll();
        List<Long> userPermissionIds = allPermissions.stream()
                .filter(p -> permissions.contains(p.code()))
                .map(Permission::id)
                .toList();

        // Get menu options accessible by user's permissions + options without permission requirement
        List<SystemMenuOption> byPermission = menuOptionRepository.findActiveByPermissionIds(userPermissionIds);
        List<SystemMenuOption> withoutPermission = menuOptionRepository.findActiveWithoutPermission();

        List<SystemMenuOption> allOptions = new ArrayList<>(byPermission);
        allOptions.addAll(withoutPermission);

        return menuOptionMapper.buildTree(allOptions);
    }
}
