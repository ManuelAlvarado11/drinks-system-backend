package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.PurchaseOrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseOrderDetailJpaRepository extends JpaRepository<PurchaseOrderDetailEntity, Long> {
    List<PurchaseOrderDetailEntity> findByPurchaseOrderId(Long purchaseOrderId);
}
