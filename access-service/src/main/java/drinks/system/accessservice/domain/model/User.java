package drinks.system.accessservice.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * Modelo de dominio que representa un usuario del sistema.
 * Contiene la información de identidad, credenciales, estado y relaciones
 * con roles y sucursales asignadas.
 */
public record User(
        Long id,
        String username,
        String passwordHash,
        String email,
        String fullName,
        Long branchId,
        Boolean isActive,
        Instant lastLogin,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt,
        Long createdBy,
        Long updatedBy,
        List<Role> roles,
        List<Branch> branches
) {
}
