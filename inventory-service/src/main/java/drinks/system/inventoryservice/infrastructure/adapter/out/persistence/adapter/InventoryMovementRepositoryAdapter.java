package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.inventoryservice.application.mapper.InventoryMovementMapper;
import drinks.system.inventoryservice.domain.model.InventoryMovement;
import drinks.system.inventoryservice.domain.port.out.InventoryMovementRepositoryPort;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository.InventoryMovementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.Instant;

@Repository @RequiredArgsConstructor
public class InventoryMovementRepositoryAdapter implements InventoryMovementRepositoryPort {
    private final InventoryMovementJpaRepository repo;
    private final InventoryMovementMapper mapper;
    @Override public InventoryMovement save(InventoryMovement m) { return mapper.toDomain(repo.save(mapper.toEntity(m))); }
    @Override public Page<InventoryMovement> findAll(Pageable p, Long productId, Long branchId, String type, Instant from, Instant to) {
        return repo.findAllFiltered(p, productId, branchId, type, from, to).map(mapper::toDomain);
    }
}
