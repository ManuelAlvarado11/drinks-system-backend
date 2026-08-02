package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.response.AuditLogResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

/**
 * Puerto de entrada para operaciones de consulta de auditoría.
 * Define el caso de uso de consulta paginada con filtros de registros de auditoría.
 */
public interface AuditUseCase {

    PageResponse<AuditLogResponse> findAll(Pageable pageable, Long userId, String module,
                                           String entityName, Instant dateFrom, Instant dateTo);
}
