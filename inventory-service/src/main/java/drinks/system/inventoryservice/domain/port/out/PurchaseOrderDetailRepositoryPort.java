package drinks.system.inventoryservice.domain.port.out;

import drinks.system.inventoryservice.domain.model.PurchaseOrderDetail;
import java.util.List;

public interface PurchaseOrderDetailRepositoryPort {
    List<PurchaseOrderDetail> saveAll(List<PurchaseOrderDetail> details);
    List<PurchaseOrderDetail> findByOrderId(Long orderId);
}
