package drinks.system.accessservice.domain.model;

import java.time.Instant;

/**
 * Modelo de dominio que representa un parámetro configurable del sistema.
 * Permite modificar el comportamiento del sistema sin cambios de código,
 * almacenando pares clave-valor con tipo de dato y módulo asociado.
 */
public record SystemParameter(
        Long id,
        String parameterKey,
        String parameterValue,
        String dataType,
        String description,
        String module,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt,
        Long createdBy,
        Long updatedBy
) {
}
