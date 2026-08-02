package drinks.system.common.audit;

/**
 * Record que representa un evento de auditoría para registrar operaciones del sistema.
 * Los campos oldValues y newValues se serializan a JSONB en la base de datos.
 *
 * @param userId      ID del usuario que realizó la acción
 * @param username    Nombre de usuario que realizó la acción
 * @param action      Tipo de acción realizada (CREATE, UPDATE, DELETE, LOGIN, LOGOUT, READ)
 * @param module      Módulo del sistema (SALES, INVENTORY, ACCESS, REPORTING)
 * @param entityName  Nombre de la entidad afectada (e.g., "Product", "Sale", "User")
 * @param entityId    ID de la entidad afectada
 * @param oldValues   Valores anteriores (se serializa a JSONB)
 * @param newValues   Valores nuevos (se serializa a JSONB)
 * @param ipAddress   Dirección IP del cliente que realizó la solicitud
 * @param description Descripción legible de la acción realizada
 */
public record AuditEvent(
        Long userId,
        String username,
        String action,
        String module,
        String entityName,
        Long entityId,
        Object oldValues,
        Object newValues,
        String ipAddress,
        String description
) {
}
