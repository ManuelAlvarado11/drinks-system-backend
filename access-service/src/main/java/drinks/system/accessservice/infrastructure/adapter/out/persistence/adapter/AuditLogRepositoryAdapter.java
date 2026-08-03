package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.application.mapper.AuditLogMapper;
import drinks.system.accessservice.domain.model.AuditLog;
import drinks.system.accessservice.domain.port.out.AuditLogRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepositoryPort {

    private final AuditLogJpaRepository auditLogJpaRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogEntity entity = auditLogMapper.toEntity(auditLog);
        AuditLogEntity saved = auditLogJpaRepository.save(entity);
        return auditLogMapper.toDomain(saved);
    }

    @Override
    public Page<AuditLog> findAll(Pageable pageable, Long userId, String module,
                                   String entityName, Instant dateFrom, Instant dateTo) {
        return auditLogJpaRepository.findAllFiltered(pageable, userId, module, entityName, dateFrom, dateTo)
                .map(auditLogMapper::toDomain);
    }
}
