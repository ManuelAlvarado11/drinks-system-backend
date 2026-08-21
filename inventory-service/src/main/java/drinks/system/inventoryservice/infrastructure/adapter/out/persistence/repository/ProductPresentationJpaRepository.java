package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.ProductPresentationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductPresentationJpaRepository extends JpaRepository<ProductPresentationEntity, Long> {
    List<ProductPresentationEntity> findByProductIdAndIsActiveTrueOrderBySortOrder(Long productId);
}
