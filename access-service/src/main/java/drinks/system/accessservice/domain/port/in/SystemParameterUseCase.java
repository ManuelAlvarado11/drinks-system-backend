package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.request.CreateSystemParameterRequest;
import drinks.system.accessservice.application.dto.request.UpdateSystemParameterRequest;
import drinks.system.accessservice.application.dto.response.SystemParameterResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada para operaciones de gestión de parámetros del sistema.
 * Define los casos de uso CRUD y búsqueda por clave de parámetros configurables.
 */
public interface SystemParameterUseCase {

    SystemParameterResponse create(CreateSystemParameterRequest request, Long currentUserId);

    PageResponse<SystemParameterResponse> findAll(Pageable pageable, String module, Boolean isActive);

    SystemParameterResponse findById(Long id);

    SystemParameterResponse findByKey(String key);

    SystemParameterResponse update(Long id, UpdateSystemParameterRequest request, Long currentUserId);

    void delete(Long id);
}
