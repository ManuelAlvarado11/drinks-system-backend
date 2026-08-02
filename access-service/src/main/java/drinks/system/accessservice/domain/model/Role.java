package drinks.system.accessservice.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * Modelo de dominio que representa un rol del sistema.
 * Un rol agrupa un conjunto de permisos y puede ser asignado a múltiples usuarios
 * para definir su nivel de acceso.
 */
public record Role(
        Long id,
        String code,
        String name,
        String description,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt,
        List<Permission> permissions
) {
}
