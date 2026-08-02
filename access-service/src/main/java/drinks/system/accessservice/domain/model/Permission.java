package drinks.system.accessservice.domain.model;

/**
 * Modelo de dominio que representa un permiso del sistema.
 * Los permisos son códigos granulares (ej. USERS_CREATE, SALES_READ) que autorizan
 * operaciones específicas y se agrupan por módulo.
 */
public record Permission(
        Long id,
        String code,
        String name,
        String description,
        String module,
        Boolean isActive
) {
}
