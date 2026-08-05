package drinks.system.inventoryservice.domain.port.out;

import drinks.system.inventoryservice.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ProductRepositoryPort {
    Optional<Product> findById(Long id);
    Product save(Product product);
    Page<Product> findAll(Pageable pageable, Long categoryId, Boolean isActive, String search);
    boolean existsByCode(String code);
}
