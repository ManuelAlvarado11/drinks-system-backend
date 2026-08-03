package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.request.CreateNotificationRequest;
import drinks.system.accessservice.application.dto.response.NotificationResponse;
import drinks.system.accessservice.application.dto.response.UnreadCountResponse;
import drinks.system.accessservice.application.mapper.NotificationMapper;
import drinks.system.accessservice.domain.model.Notification;
import drinks.system.accessservice.domain.port.in.NotificationUseCase;
import drinks.system.accessservice.domain.port.out.NotificationRepositoryPort;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.exception.ForbiddenException;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationUseCase {

    private final NotificationRepositoryPort notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> findByUser(Long userId, Pageable pageable, Boolean isRead, String type) {
        Page<Notification> page = notificationRepository.findByUserId(userId, pageable, isRead, type);
        List<NotificationResponse> content = page.getContent().stream()
                .map(notificationMapper::toResponse)
                .toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(Long userId) {
        long count = notificationRepository.countUnreadByUserId(userId);
        return new UnreadCountResponse(count);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación", notificationId));

        if (!notification.userId().equals(userId)) {
            throw new ForbiddenException("No tiene permiso para marcar esta notificación como leída");
        }

        Notification updated = new Notification(
                notification.id(), notification.branchId(), notification.userId(),
                notification.notificationType(), notification.title(), notification.message(),
                notification.entityName(), notification.entityId(), true,
                notification.createdAt(), java.time.Instant.now()
        );

        notificationRepository.save(updated);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    @Transactional
    public NotificationResponse create(CreateNotificationRequest request) {
        Notification notification = new Notification(
                null, request.branchId(), request.userId(), request.notificationType(),
                request.title(), request.message(), null, null,
                false, null, null
        );

        Notification saved = notificationRepository.save(notification);
        return notificationMapper.toResponse(saved);
    }
}
