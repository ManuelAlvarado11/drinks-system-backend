package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.request.CreateMenuOptionRequest;
import drinks.system.accessservice.application.dto.request.UpdateMenuOptionRequest;
import drinks.system.accessservice.application.dto.response.MenuOptionResponse;
import drinks.system.accessservice.application.dto.response.MenuTreeResponse;

import java.util.List;

/**
 * Puerto de entrada para operaciones de gestión de opciones de menú.
 * Define los casos de uso CRUD y construcción del árbol de menú personalizado.
 */
public interface MenuOptionUseCase {

    MenuOptionResponse create(CreateMenuOptionRequest request);

    List<MenuOptionResponse> findAll();

    MenuOptionResponse findById(Long id);

    MenuOptionResponse update(Long id, UpdateMenuOptionRequest request);

    void delete(Long id);

    List<MenuTreeResponse> getMyMenu(Long userId, List<String> permissions);
}
