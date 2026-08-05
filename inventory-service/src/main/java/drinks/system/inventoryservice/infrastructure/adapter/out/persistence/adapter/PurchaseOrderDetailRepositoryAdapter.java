package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.inventoryservice.application.mapper.PurchaseOrderMapper;
import drinks.system.inventoryservice.domain.model.PurchaseOrderDetail;
import drinks.system.inventoryservice.domain.port.out.PurchaseOrderDetailRepositoryPort;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository.PurchaseOrderDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository @RequiredArgsConstructor
public class PurchaseOrderDetailRepositoryAdapter implements PurchaseOrderDetailRepositoryPort {
    private final PurchaseOrderDetailJpaRepository repo;
    private final PurchaseOrderMapper mapper;
    @Override public List<PurchaseOrderDetail> saveAll(List<PurchaseOrderDetail> details) {
        return repo.saveAll(details.stream().map(mapper::detailToEntity).toList()).stream().map(mapper::detailToDomain).toList();
    }
    @Override public List<PurchaseOrderDetail> findByOrderId(Long orderId) {
        return repo.findByPurchaseOrderId(orderId).stream().map(mapper::detailToDomain).toList();
    }
}
