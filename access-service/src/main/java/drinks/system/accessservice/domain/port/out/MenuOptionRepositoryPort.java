package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.SystemMenuOption;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para acceso a datos de opciones de menú del sistema.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface MenuOptionRepositoryPort {

    Optional<SystemMenuOption> findById(Long id);

    SystemMenuOption save(SystemMenuOption menuOption);

    List<SystemMenuOption> findAll();

    List<SystemMenuOption> findActiveByPermissionIds(List<Long> permissionIds);

    List<SystemMenuOption> findActiveWithoutPermission();
}
