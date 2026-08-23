package drinks.system.inventoryservice.application.mapper;

import drinks.system.inventoryservice.application.dto.response.InventoryMovementResponse;
import drinks.system.inventoryservice.domain.model.InventoryMovement;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.InventoryMovementEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryMovementMapper {
    public InventoryMovement toDomain(InventoryMovementEntity e) {
        return new InventoryMovement(e.getId(), e.getProductId(), e.getBranchId(), e.getMovementType(),
                e.getQuantity(), e.getPreviousStock(), e.getNewStock(), e.getReferenceType(),
                e.getReferenceId(), e.getNotes(), e.getCreatedAt(), e.getCreatedBy());
    }
    public InventoryMovementEntity toEntity(InventoryMovement d) {
        InventoryMovementEntity e = new InventoryMovementEntity();
        e.setProductId(d.productId()); e.setBranchId(d.branchId()); e.setMovementType(d.movementType());
        e.setQuantity(d.quantity()); e.setPreviousStock(d.previousStock()); e.setNewStock(d.newStock());
        e.setReferenceType(d.referenceType()); e.setReferenceId(d.referenceId());
        e.setNotes(d.notes()); e.setCreatedBy(d.createdBy());
        return e;
    }
    public InventoryMovementResponse toResponse(InventoryMovement m) {
        return new InventoryMovementResponse(m.id(), m.productId(), m.branchId(),
                null, null,
                m.movementType(), m.quantity(), m.previousStock(), m.newStock(),
                m.referenceType(), m.referenceId(), m.notes(), m.createdAt(), m.createdBy());
    }

    public InventoryMovementResponse toResponse(InventoryMovement m, String productName, String branchName) {
        return new InventoryMovementResponse(m.id(), m.productId(), m.branchId(),
                productName, branchName,
                m.movementType(), m.quantity(), m.previousStock(), m.newStock(),
                m.referenceType(), m.referenceId(), m.notes(), m.createdAt(), m.createdBy());
    }
}
