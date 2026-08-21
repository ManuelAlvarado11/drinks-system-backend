package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.inventoryservice.application.mapper.ProductPresentationMapper;
import drinks.system.inventoryservice.domain.model.ProductPresentation;
import drinks.system.inventoryservice.domain.port.out.ProductPresentationRepositoryPort;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository.ProductPresentationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class ProductPresentationRepositoryAdapter implements ProductPresentationRepositoryPort {
    private final ProductPresentationJpaRepository repo;
    private final ProductPresentationMapper mapper;

    @Override
    public List<ProductPresentation> findByProductId(Long productId) {
        return repo.findByProductIdAndIsActiveTrueOrderBySortOrder(productId).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ProductPresentation> findById(Long id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public ProductPresentation save(ProductPresentation presentation) {
        return mapper.toDomain(repo.save(mapper.toEntity(presentation)));
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
