package drinks.system.accessservice.application.mapper;

import drinks.system.accessservice.application.dto.response.NotificationResponse;
import drinks.system.accessservice.domain.model.Notification;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toDomain(NotificationEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getBranchId(),
                entity.getUserId(),
                entity.getNotificationType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getEntityName(),
                entity.getEntityId(),
                entity.getIsRead(),
                entity.getCreatedAt(),
                entity.getReadAt()
        );
    }

    public NotificationEntity toEntity(Notification domain) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(domain.id());
        entity.setBranchId(domain.branchId());
        entity.setUserId(domain.userId());
        entity.setNotificationType(domain.notificationType());
        entity.setTitle(domain.title());
        entity.setMessage(domain.message());
        entity.setEntityName(domain.entityName());
        entity.setEntityId(domain.entityId());
        entity.setIsRead(domain.isRead());
        return entity;
    }

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.id(),
                notification.branchId(),
                notification.userId(),
                notification.notificationType(),
                notification.title(),
                notification.message(),
                notification.entityName(),
                notification.entityId(),
                notification.isRead(),
                notification.createdAt(),
                notification.readAt()
        );
    }
}
