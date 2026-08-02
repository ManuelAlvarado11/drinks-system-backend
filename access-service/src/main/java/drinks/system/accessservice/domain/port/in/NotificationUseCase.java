package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.request.CreateNotificationRequest;
import drinks.system.accessservice.application.dto.response.NotificationResponse;
import drinks.system.accessservice.application.dto.response.UnreadCountResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada para operaciones de gestión de notificaciones.
 * Define los casos de uso de consulta, lectura y creación de notificaciones.
 */
public interface NotificationUseCase {

    PageResponse<NotificationResponse> findByUser(Long userId, Pageable pageable, Boolean isRead, String type);

    UnreadCountResponse getUnreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    NotificationResponse create(CreateNotificationRequest request);
}
