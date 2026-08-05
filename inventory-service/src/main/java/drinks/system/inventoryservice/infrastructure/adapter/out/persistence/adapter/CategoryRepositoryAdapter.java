package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.inventoryservice.application.mapper.CategoryMapper;
import drinks.system.inventoryservice.domain.model.Category;
import drinks.system.inventoryservice.domain.port.out.CategoryRepositoryPort;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {
    private final CategoryJpaRepository repo;
    private final CategoryMapper mapper;
    @Override public Optional<Category> findById(Long id) { return repo.findById(id).map(mapper::toDomain); }
    @Override public Category save(Category c) { return mapper.toDomain(repo.save(mapper.toEntity(c))); }
    @Override public List<Category> findAll(Boolean isActive) {
        if (Boolean.TRUE.equals(isActive)) return repo.findAllByIsActiveTrue().stream().map(mapper::toDomain).toList();
        return repo.findAllByOrderByNameAsc().stream().map(mapper::toDomain).toList();
    }
}
