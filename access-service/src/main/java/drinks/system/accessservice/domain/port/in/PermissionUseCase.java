package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.response.PermissionResponse;
import drinks.system.accessservice.application.dto.response.PermissionsByModuleResponse;

import java.util.List;

/**
 * Puerto de entrada para operaciones de consulta de permisos.
 * Define los casos de uso de listado completo y agrupación por módulo.
 */
public interface PermissionUseCase {

    List<PermissionResponse> findAll();

    List<PermissionsByModuleResponse> findGroupedByModule();
}
