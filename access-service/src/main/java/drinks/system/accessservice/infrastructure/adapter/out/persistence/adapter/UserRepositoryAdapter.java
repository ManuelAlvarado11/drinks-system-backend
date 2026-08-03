package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.application.mapper.UserMapper;
import drinks.system.accessservice.domain.model.User;
import drinks.system.accessservice.domain.port.out.UserRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userJpaRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public Page<User> findAll(Pageable pageable, Boolean isActive, Long branchId, String search) {
        return userJpaRepository.findAllFiltered(pageable, isActive, branchId, search)
                .map(userMapper::toDomain);
    }

    @Override
    public void updateLastLogin(Long userId, Instant lastLogin) {
        userJpaRepository.updateLastLogin(userId, lastLogin);
    }
}
