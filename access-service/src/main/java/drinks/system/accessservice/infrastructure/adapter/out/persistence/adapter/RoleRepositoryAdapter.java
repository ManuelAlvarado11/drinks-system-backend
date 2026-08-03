package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.application.mapper.RoleMapper;
import drinks.system.accessservice.domain.model.Role;
import drinks.system.accessservice.domain.port.out.RoleRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper roleMapper;

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id).map(roleMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return roleJpaRepository.existsByCode(code);
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity = roleMapper.toEntity(role);
        RoleEntity saved = roleJpaRepository.save(entity);
        return roleMapper.toDomain(saved);
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        return roleJpaRepository.findAll(pageable).map(roleMapper::toDomain);
    }

    @Override
    public List<Role> findByUserId(Long userId) {
        return roleJpaRepository.findByUserId(userId).stream()
                .map(roleMapper::toDomain)
                .toList();
    }
}
