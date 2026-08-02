package drinks.system.accessservice.domain.model;

import java.time.Instant;

/**
 * Modelo de dominio que representa una opción del menú de navegación del sistema.
 * Las opciones se organizan jerárquicamente por parentId y se vinculan a permisos
 * para controlar la visibilidad según el rol del usuario.
 */
public record SystemMenuOption(
        Long id,
        String name,
        String route,
        String icon,
        Long parentId,
        Long permissionId,
        Integer sortOrder,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
