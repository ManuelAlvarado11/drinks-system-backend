package drinks.system.accessservice.domain.model;

import java.time.Instant;

/**
 * Modelo de dominio que representa un refresh token.
 * Almacena el hash SHA-256 del token opaco utilizado para renovar el JWT
 * sin requerir re-autenticación del usuario.
 */
public record RefreshToken(
        Long id,
        Long userId,
        String tokenHash,
        Instant expiresAt,
        Boolean isRevoked,
        String deviceInfo,
        Instant createdAt
) {
}
