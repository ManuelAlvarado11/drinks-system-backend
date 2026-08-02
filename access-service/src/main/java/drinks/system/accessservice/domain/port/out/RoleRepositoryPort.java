package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para acceso a datos de roles.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface RoleRepositoryPort {

    Optional<Role> findById(Long id);

    boolean existsByCode(String code);

    Role save(Role role);

    Page<Role> findAll(Pageable pageable);

    List<Role> findByUserId(Long userId);
}
