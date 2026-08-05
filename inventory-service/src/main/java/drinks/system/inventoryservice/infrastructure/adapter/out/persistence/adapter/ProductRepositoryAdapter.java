package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.inventoryservice.application.mapper.ProductMapper;
import drinks.system.inventoryservice.domain.model.Product;
import drinks.system.inventoryservice.domain.port.out.ProductRepositoryPort;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    private final ProductJpaRepository repo;
    private final ProductMapper mapper;
    @Override public Optional<Product> findById(Long id) { return repo.findById(id).map(mapper::toDomain); }
    @Override public Product save(Product p) { return mapper.toDomain(repo.save(mapper.toEntity(p))); }
    @Override public Page<Product> findAll(Pageable pg, Long catId, Boolean active, String search) {
        return repo.findAllFiltered(pg, catId, active, search).map(mapper::toDomain);
    }
    @Override public boolean existsByCode(String code) { return repo.existsByCode(code); }
}
