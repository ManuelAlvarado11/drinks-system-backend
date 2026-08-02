package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.RefreshToken;

import java.util.Optional;

/**
 * Puerto de salida para acceso a datos de refresh tokens.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface RefreshTokenRepositoryPort {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken refreshToken);

    void revokeByTokenHash(String tokenHash);

    void revokeAllByUserId(Long userId);
}
