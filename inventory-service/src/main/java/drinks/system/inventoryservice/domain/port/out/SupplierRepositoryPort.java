package drinks.system.inventoryservice.domain.port.out;

import drinks.system.inventoryservice.domain.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface SupplierRepositoryPort {
    Optional<Supplier> findById(Long id);
    Supplier save(Supplier supplier);
    Page<Supplier> findAll(Pageable pageable, String search);
}
