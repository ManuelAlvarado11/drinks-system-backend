package drinks.system.accessservice.domain.model;

import java.time.Instant;

/**
 * Modelo de dominio que representa una sucursal (ubicación física del bar).
 * Los datos operacionales del sistema se asocian a una sucursal específica.
 */
public record Branch(
        Long id,
        String name,
        String address,
        String phone,
        String email,
        Boolean isActive,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt,
        Long createdBy,
        Long updatedBy
) {
}
