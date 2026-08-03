package drinks.system.accessservice.application.mapper;

import drinks.system.accessservice.application.dto.response.CatalogResponse;
import drinks.system.accessservice.domain.model.Catalog;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.CatalogEntity;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {

    public Catalog toDomain(CatalogEntity entity) {
        return new Catalog(
                entity.getId(),
                entity.getCatalogType(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getSortOrder(),
                entity.getIsActive(),
                entity.getParentId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public CatalogEntity toEntity(Catalog domain) {
        CatalogEntity entity = new CatalogEntity();
        entity.setId(domain.id());
        entity.setCatalogType(domain.catalogType());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setSortOrder(domain.sortOrder());
        entity.setIsActive(domain.isActive());
        entity.setParentId(domain.parentId());
        return entity;
    }

    public CatalogResponse toResponse(Catalog catalog) {
        return new CatalogResponse(
                catalog.id(),
                catalog.catalogType(),
                catalog.code(),
                catalog.name(),
                catalog.description(),
                catalog.sortOrder(),
                catalog.isActive(),
                catalog.parentId(),
                catalog.createdAt(),
                catalog.updatedAt()
        );
    }
}
