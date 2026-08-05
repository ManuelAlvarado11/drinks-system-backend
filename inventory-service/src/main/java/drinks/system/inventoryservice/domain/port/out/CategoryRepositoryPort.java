package drinks.system.inventoryservice.domain.port.out;

import drinks.system.inventoryservice.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Optional<Category> findById(Long id);
    Category save(Category category);
    List<Category> findAll(Boolean isActive);
}
