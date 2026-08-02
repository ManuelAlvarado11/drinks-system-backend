package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.Permission;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para acceso a datos de permisos.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface PermissionRepositoryPort {

    List<Permission> findAll();

    List<Permission> findByRoleIds(List<Long> roleIds);

    List<Permission> findByIds(List<Long> ids);

    Optional<Permission> findById(Long id);
}
