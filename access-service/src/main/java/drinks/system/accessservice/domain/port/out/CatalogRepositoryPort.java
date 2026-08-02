package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.Catalog;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para acceso a datos de catálogos.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface CatalogRepositoryPort {

    Optional<Catalog> findById(Long id);

    boolean existsByTypeAndCode(String type, String code);

    Catalog save(Catalog catalog);

    List<Catalog> findByType(String catalogType);

    List<String> findDistinctTypes();
}
