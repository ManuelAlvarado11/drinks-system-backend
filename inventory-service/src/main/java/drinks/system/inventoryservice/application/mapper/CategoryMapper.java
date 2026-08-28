package drinks.system.inventoryservice.application.mapper;

import drinks.system.inventoryservice.application.dto.response.CategoryResponse;
import drinks.system.inventoryservice.domain.model.Category;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Category toDomain(CategoryEntity e) {
        return new Category(e.getId(), e.getName(), e.getDescription(), e.getIcon(), e.getParentCategoryId(),
                e.getIsActive(), e.getDeletedAt(), e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy());
    }
    public CategoryEntity toEntity(Category d) {
        CategoryEntity e = new CategoryEntity();
        e.setId(d.id()); e.setName(d.name()); e.setDescription(d.description()); e.setIcon(d.icon());
        e.setParentCategoryId(d.parentCategoryId()); e.setIsActive(d.isActive());
        e.setDeletedAt(d.deletedAt()); e.setCreatedBy(d.createdBy()); e.setUpdatedBy(d.updatedBy());
        return e;
    }
    public CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.id(), c.name(), c.description(), c.icon(), c.parentCategoryId(),
                c.isActive(), c.createdAt(), c.updatedAt());
    }
}
