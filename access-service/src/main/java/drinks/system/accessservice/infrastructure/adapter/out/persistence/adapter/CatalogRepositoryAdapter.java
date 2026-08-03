package drinks.system.accessservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.accessservice.application.mapper.CatalogMapper;
import drinks.system.accessservice.domain.model.Catalog;
import drinks.system.accessservice.domain.port.out.CatalogRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.CatalogEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.CatalogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CatalogRepositoryAdapter implements CatalogRepositoryPort {

    private final CatalogJpaRepository catalogJpaRepository;
    private final CatalogMapper catalogMapper;

    @Override
    public Optional<Catalog> findById(Long id) {
        return catalogJpaRepository.findById(id).map(catalogMapper::toDomain);
    }

    @Override
    public boolean existsByTypeAndCode(String type, String code) {
        return catalogJpaRepository.existsByCatalogTypeAndCode(type, code);
    }

    @Override
    public Catalog save(Catalog catalog) {
        CatalogEntity entity = catalogMapper.toEntity(catalog);
        CatalogEntity saved = catalogJpaRepository.save(entity);
        return catalogMapper.toDomain(saved);
    }

    @Override
    public List<Catalog> findByType(String catalogType) {
        return catalogJpaRepository.findByCatalogTypeAndIsActiveTrueOrderBySortOrderAsc(catalogType).stream()
                .map(catalogMapper::toDomain)
                .toList();
    }

    @Override
    public List<String> findDistinctTypes() {
        return catalogJpaRepository.findDistinctCatalogTypes();
    }
}
