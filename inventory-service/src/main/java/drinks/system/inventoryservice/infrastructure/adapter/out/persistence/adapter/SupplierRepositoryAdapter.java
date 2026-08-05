package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.inventoryservice.application.mapper.SupplierMapper;
import drinks.system.inventoryservice.domain.model.Supplier;
import drinks.system.inventoryservice.domain.port.out.SupplierRepositoryPort;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository.SupplierJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class SupplierRepositoryAdapter implements SupplierRepositoryPort {
    private final SupplierJpaRepository repo;
    private final SupplierMapper mapper;
    @Override public Optional<Supplier> findById(Long id) { return repo.findById(id).map(mapper::toDomain); }
    @Override public Supplier save(Supplier s) { return mapper.toDomain(repo.save(mapper.toEntity(s))); }
    @Override public Page<Supplier> findAll(Pageable p, String search) { return repo.findAllFiltered(p, search).map(mapper::toDomain); }
}
