package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.application.mapper.NotificationMapper;
import drinks.system.accessservice.domain.model.Notification;
import drinks.system.accessservice.domain.port.out.NotificationRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.NotificationEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id).map(notificationMapper::toDomain);
    }

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = notificationMapper.toEntity(notification);
        NotificationEntity saved = notificationJpaRepository.save(entity);
        return notificationMapper.toDomain(saved);
    }

    @Override
    public Page<Notification> findByUserId(Long userId, Pageable pageable, Boolean isRead, String type) {
        return notificationJpaRepository.findByUserFiltered(userId, pageable, isRead, type)
                .map(notificationMapper::toDomain);
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        return notificationJpaRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public void markAllAsReadByUserId(Long userId) {
        notificationJpaRepository.markAllAsReadByUserId(userId);
    }
}
