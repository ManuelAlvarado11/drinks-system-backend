package drinks.system.common.audit;

/**
 * Interfaz marcadora para entidades que deben ser auditadas.
 * Las entidades de dominio que implementen esta interfaz indican que
 * sus operaciones de escritura (CREATE, UPDATE, DELETE) deben generar
 * eventos de auditoría.
 */
public interface Auditable {
}
