package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.request.CreateCatalogRequest;
import drinks.system.accessservice.application.dto.request.UpdateCatalogRequest;
import drinks.system.accessservice.application.dto.response.CatalogResponse;

import java.util.List;

/**
 * Puerto de entrada para operaciones de gestión de catálogos.
 * Define los casos de uso CRUD, consulta por tipo y listado de tipos distintos.
 */
public interface CatalogUseCase {

    CatalogResponse create(CreateCatalogRequest request);

    List<CatalogResponse> findByType(String catalogType);

    List<String> findDistinctTypes();

    CatalogResponse findById(Long id);

    CatalogResponse update(Long id, UpdateCatalogRequest request);

    void delete(Long id);
}
