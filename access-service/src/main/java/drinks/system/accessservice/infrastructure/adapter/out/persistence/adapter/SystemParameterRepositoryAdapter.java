package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.application.mapper.SystemParameterMapper;
import drinks.system.accessservice.domain.model.SystemParameter;
import drinks.system.accessservice.domain.port.out.SystemParameterRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.SystemParameterEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.SystemParameterJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SystemParameterRepositoryAdapter implements SystemParameterRepositoryPort {

    private final SystemParameterJpaRepository systemParameterJpaRepository;
    private final SystemParameterMapper systemParameterMapper;

    @Override
    public Optional<SystemParameter> findById(Long id) {
        return systemParameterJpaRepository.findById(id).map(systemParameterMapper::toDomain);
    }

    @Override
    public Optional<SystemParameter> findByKey(String key) {
        return systemParameterJpaRepository.findByParameterKey(key).map(systemParameterMapper::toDomain);
    }

    @Override
    public boolean existsByKey(String key) {
        return systemParameterJpaRepository.existsByParameterKey(key);
    }

    @Override
    public SystemParameter save(SystemParameter param) {
        SystemParameterEntity entity = systemParameterMapper.toEntity(param);
        SystemParameterEntity saved = systemParameterJpaRepository.save(entity);
        return systemParameterMapper.toDomain(saved);
    }

    @Override
    public Page<SystemParameter> findAll(Pageable pageable, String module, Boolean isActive) {
        return systemParameterJpaRepository.findAllFiltered(pageable, module, isActive)
                .map(systemParameterMapper::toDomain);
    }
}
