package drinks.system.inventoryservice.application.mapper;

import drinks.system.inventoryservice.application.dto.response.ProductPresentationResponse;
import drinks.system.inventoryservice.domain.model.ProductPresentation;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.ProductPresentationEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductPresentationMapper {

    public ProductPresentation toDomain(ProductPresentationEntity e) {
        return new ProductPresentation(e.getId(), e.getProductId(), e.getName(),
                e.getQuantity(), e.getPrice(), e.getIsActive(), e.getSortOrder(),
                e.getCreatedAt(), e.getUpdatedAt());
    }

    public ProductPresentationEntity toEntity(ProductPresentation d) {
        ProductPresentationEntity e = new ProductPresentationEntity();
        e.setId(d.id());
        e.setProductId(d.productId());
        e.setName(d.name());
        e.setQuantity(d.quantity());
        e.setPrice(d.price());
        e.setIsActive(d.isActive());
        e.setSortOrder(d.sortOrder());
        return e;
    }

    public ProductPresentationResponse toResponse(ProductPresentation p) {
        BigDecimal unitPrice = p.price().divide(BigDecimal.valueOf(p.quantity()), 2, RoundingMode.HALF_UP);
        return new ProductPresentationResponse(p.id(), p.productId(), p.name(),
                p.quantity(), p.price(), unitPrice, p.isActive(), p.sortOrder());
    }
}
