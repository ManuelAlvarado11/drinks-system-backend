package drinks.system.inventoryservice.domain.port.out;

import drinks.system.inventoryservice.domain.model.ProductPresentation;
import java.util.List;
import java.util.Optional;

public interface ProductPresentationRepositoryPort {
    List<ProductPresentation> findByProductId(Long productId);
    Optional<ProductPresentation> findById(Long id);
    ProductPresentation save(ProductPresentation presentation);
    void deleteById(Long id);
}
