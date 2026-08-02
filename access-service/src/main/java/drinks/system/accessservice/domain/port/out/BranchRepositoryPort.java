package drinks.system.accessservice.domain.port.out;

import drinks.system.accessservice.domain.model.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Puerto de salida para acceso a datos de sucursales.
 * Define el contrato de persistencia usando exclusivamente tipos del dominio.
 */
public interface BranchRepositoryPort {

    Optional<Branch> findById(Long id);

    Branch save(Branch branch);

    Page<Branch> findAll(Pageable pageable, Boolean isActive);
}
