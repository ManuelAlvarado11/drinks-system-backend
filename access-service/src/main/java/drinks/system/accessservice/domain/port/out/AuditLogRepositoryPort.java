package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

/**
 * Puerto de salida para acceso a datos de registros de auditoría.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface AuditLogRepositoryPort {

    AuditLog save(AuditLog auditLog);

    Page<AuditLog> findAll(Pageable pageable, Long userId, String module,
                           String entityName, Instant dateFrom, Instant dateTo);
}
