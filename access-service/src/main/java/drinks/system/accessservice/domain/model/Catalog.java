package drinks.system.accessservice.domain.model;

import java.time.Instant;

/**
 * Modelo de dominio que representa un catálogo del sistema.
 * Los catálogos son listas de valores enumerables configurables agrupados por tipo
 * (ej. métodos de pago, tipos de movimiento), mantenidos sin modificar código.
 */
public record Catalog(
        Long id,
        String catalogType,
        String code,
        String name,
        String description,
        Integer sortOrder,
        Boolean isActive,
        Long parentId,
        Instant createdAt,
        Instant updatedAt
) {
}
