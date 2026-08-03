package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.mapper.AuditLogMapper;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.AuditLogJpaRepository;
import drinks.system.common.audit.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditLogJpaRepository auditLogJpaRepository;
    private final AuditLogMapper auditLogMapper;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditEvent(AuditEvent event) {
        try {
            AuditLogEntity entity = auditLogMapper.fromEvent(event);
            auditLogJpaRepository.save(entity);
        } catch (Exception e) {
            log.error("Error al guardar registro de auditoría: {}", e.getMessage(), e);
        }
    }
}
