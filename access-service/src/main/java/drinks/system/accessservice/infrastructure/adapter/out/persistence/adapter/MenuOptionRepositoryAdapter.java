package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.application.mapper.MenuOptionMapper;
import drinks.system.accessservice.domain.model.SystemMenuOption;
import drinks.system.accessservice.domain.port.out.MenuOptionRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.SystemMenuOptionEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.SystemMenuOptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MenuOptionRepositoryAdapter implements MenuOptionRepositoryPort {

    private final SystemMenuOptionJpaRepository menuOptionJpaRepository;
    private final MenuOptionMapper menuOptionMapper;

    @Override
    public Optional<SystemMenuOption> findById(Long id) {
        return menuOptionJpaRepository.findById(id).map(menuOptionMapper::toDomain);
    }

    @Override
    public SystemMenuOption save(SystemMenuOption menuOption) {
        SystemMenuOptionEntity entity = menuOptionMapper.toEntity(menuOption);
        SystemMenuOptionEntity saved = menuOptionJpaRepository.save(entity);
        return menuOptionMapper.toDomain(saved);
    }

    @Override
    public List<SystemMenuOption> findAll() {
        return menuOptionJpaRepository.findAll().stream()
                .map(menuOptionMapper::toDomain)
                .toList();
    }

    @Override
    public List<SystemMenuOption> findActiveByPermissionIds(List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return List.of();
        }
        return menuOptionJpaRepository.findActiveByPermissionIds(permissionIds).stream()
                .map(menuOptionMapper::toDomain)
                .toList();
    }

    @Override
    public List<SystemMenuOption> findActiveWithoutPermission() {
        return menuOptionJpaRepository.findAllByIsActiveTrueAndPermissionIdIsNull().stream()
                .map(menuOptionMapper::toDomain)
                .toList();
    }
}
