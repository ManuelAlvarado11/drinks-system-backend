package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.inventoryservice.application.mapper.PurchaseOrderMapper;
import drinks.system.inventoryservice.domain.model.PurchaseOrder;
import drinks.system.inventoryservice.domain.port.out.PurchaseOrderRepositoryPort;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository.PurchaseOrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class PurchaseOrderRepositoryAdapter implements PurchaseOrderRepositoryPort {
    private final PurchaseOrderJpaRepository repo;
    private final PurchaseOrderMapper mapper;
    @Override public Optional<PurchaseOrder> findById(Long id) { return repo.findById(id).map(mapper::toDomain); }
    @Override public PurchaseOrder save(PurchaseOrder o) { return mapper.toDomain(repo.save(mapper.toEntity(o))); }
    @Override public Page<PurchaseOrder> findAll(Pageable p, Long supplierId, Long branchId, String status, Instant from, Instant to) {
        return repo.findAllFiltered(p, supplierId, branchId, status, from, to).map(mapper::toDomain);
    }
    @Override public String generateOrderNumber() {
        long count = repo.count();
        return String.format("OC-%06d", count + 1);
    }
}
