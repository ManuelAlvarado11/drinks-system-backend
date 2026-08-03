package drinks.system.accessservice.application.mapper;

import drinks.system.accessservice.application.dto.response.AuditLogResponse;
import drinks.system.accessservice.domain.model.AuditLog;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import drinks.system.common.audit.AuditEvent;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLog toDomain(AuditLogEntity entity) {
        return new AuditLog(
                entity.getId(),
                entity.getUserId(),
                entity.getUsername(),
                entity.getAction(),
                entity.getModule(),
                entity.getEntityName(),
                entity.getEntityId(),
                entity.getOldValues(),
                entity.getNewValues(),
                entity.getIpAddress(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }

    public AuditLogEntity toEntity(AuditLog domain) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUserId(domain.userId());
        entity.setUsername(domain.username());
        entity.setAction(domain.action());
        entity.setModule(domain.module());
        entity.setEntityName(domain.entityName());
        entity.setEntityId(domain.entityId());
        entity.setOldValues(domain.oldValues());
        entity.setNewValues(domain.newValues());
        entity.setIpAddress(domain.ipAddress());
        entity.setDescription(domain.description());
        return entity;
    }

    public AuditLogEntity fromEvent(AuditEvent event) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setUserId(event.userId());
        entity.setUsername(event.username());
        entity.setAction(event.action());
        entity.setModule(event.module());
        entity.setEntityName(event.entityName());
        entity.setEntityId(event.entityId());
        entity.setOldValues(event.oldValues() != null ? event.oldValues().toString() : null);
        entity.setNewValues(event.newValues() != null ? event.newValues().toString() : null);
        entity.setIpAddress(event.ipAddress());
        entity.setDescription(event.description());
        return entity;
    }

    public AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.id(),
                log.userId(),
                log.username(),
                log.action(),
                log.module(),
                log.entityName(),
                log.entityId(),
                log.oldValues(),
                log.newValues(),
                log.ipAddress(),
                log.description(),
                log.createdAt()
        );
    }
}
