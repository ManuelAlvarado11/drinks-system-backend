package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.domain.model.RefreshToken;
import drinks.system.accessservice.domain.port.out.RefreshTokenRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenJpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = toEntity(refreshToken);
        RefreshTokenEntity saved = refreshTokenJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void revokeByTokenHash(String tokenHash) {
        refreshTokenJpaRepository.findByTokenHash(tokenHash).ifPresent(entity -> {
            entity.setIsRevoked(true);
            refreshTokenJpaRepository.save(entity);
        });
    }

    @Override
    public void revokeAllByUserId(Long userId) {
        refreshTokenJpaRepository.revokeAllByUserId(userId);
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return new RefreshToken(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.getIsRevoked(),
                entity.getDeviceInfo(),
                entity.getCreatedAt()
        );
    }

    private RefreshTokenEntity toEntity(RefreshToken domain) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(domain.id());
        entity.setUserId(domain.userId());
        entity.setTokenHash(domain.tokenHash());
        entity.setExpiresAt(domain.expiresAt());
        entity.setIsRevoked(domain.isRevoked());
        entity.setDeviceInfo(domain.deviceInfo());
        return entity;
    }
}
