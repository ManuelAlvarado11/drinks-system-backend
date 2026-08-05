package drinks.system.inventoryservice.application.mapper;

import drinks.system.inventoryservice.application.dto.response.ProductResponse;
import drinks.system.inventoryservice.domain.model.Product;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toDomain(ProductEntity e) {
        return new Product(e.getId(), e.getCode(), e.getName(), e.getCategoryId(), e.getSize(),
                e.getDescription(), e.getCostPrice(), e.getSalePrice(), e.getIsActive(),
                e.getDeletedAt(), e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy());
    }
    public ProductEntity toEntity(Product d) {
        ProductEntity e = new ProductEntity();
        e.setId(d.id()); e.setCode(d.code()); e.setName(d.name()); e.setCategoryId(d.categoryId());
        e.setSize(d.size()); e.setDescription(d.description()); e.setCostPrice(d.costPrice());
        e.setSalePrice(d.salePrice()); e.setIsActive(d.isActive()); e.setDeletedAt(d.deletedAt());
        e.setCreatedBy(d.createdBy()); e.setUpdatedBy(d.updatedBy());
        return e;
    }
    public ProductResponse toResponse(Product p) {
        return new ProductResponse(p.id(), p.code(), p.name(), p.categoryId(), p.size(),
                p.description(), p.costPrice(), p.salePrice(), p.isActive(), p.createdAt(), p.updatedAt());
    }
}
