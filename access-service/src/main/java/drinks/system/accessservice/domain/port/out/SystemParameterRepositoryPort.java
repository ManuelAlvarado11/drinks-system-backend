package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.SystemParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Puerto de salida para acceso a datos de parámetros del sistema.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface SystemParameterRepositoryPort {

    Optional<SystemParameter> findById(Long id);

    Optional<SystemParameter> findByKey(String key);

    boolean existsByKey(String key);

    SystemParameter save(SystemParameter param);

    Page<SystemParameter> findAll(Pageable pageable, String module, Boolean isActive);
}
