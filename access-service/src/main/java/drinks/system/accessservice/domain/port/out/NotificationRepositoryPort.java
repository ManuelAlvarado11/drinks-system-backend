package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Puerto de salida para acceso a datos de notificaciones.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface NotificationRepositoryPort {

    Optional<Notification> findById(Long id);

    Notification save(Notification notification);

    Page<Notification> findByUserId(Long userId, Pageable pageable, Boolean isRead, String type);

    long countUnreadByUserId(Long userId);

    void markAllAsReadByUserId(Long userId);
}
