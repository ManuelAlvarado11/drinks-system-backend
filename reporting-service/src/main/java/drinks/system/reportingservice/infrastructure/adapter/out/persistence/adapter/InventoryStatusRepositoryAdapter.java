package drinks.system.reportingservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.reportingservice.application.mapper.ReportingMapper;
import drinks.system.reportingservice.domain.model.InventoryStatus;
import drinks.system.reportingservice.domain.port.out.InventoryStatusRepositoryPort;
import drinks.system.reportingservice.infrastructure.adapter.out.persistence.repository.InventoryStatusJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository @RequiredArgsConstructor
public class InventoryStatusRepositoryAdapter implements InventoryStatusRepositoryPort {
    private final InventoryStatusJpaRepository repo;
    private final ReportingMapper mapper;
    @Override
    public Page<InventoryStatus> findAll(Pageable p, Long branchId, Boolean lowStockOnly) {
        return repo.findAllFiltered(p, branchId, lowStockOnly).map(mapper::inventoryToDomain);
    }
}
