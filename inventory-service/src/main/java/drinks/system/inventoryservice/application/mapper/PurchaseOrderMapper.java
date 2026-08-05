package drinks.system.inventoryservice.application.mapper;

import drinks.system.inventoryservice.application.dto.response.PurchaseOrderDetailResponse;
import drinks.system.inventoryservice.application.dto.response.PurchaseOrderResponse;
import drinks.system.inventoryservice.domain.model.PurchaseOrder;
import drinks.system.inventoryservice.domain.model.PurchaseOrderDetail;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.PurchaseOrderDetailEntity;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.PurchaseOrderEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PurchaseOrderMapper {
    public PurchaseOrder toDomain(PurchaseOrderEntity e) {
        return new PurchaseOrder(e.getId(), e.getSupplierId(), e.getBranchId(), e.getOrderNumber(),
                e.getStatus(), e.getTotalAmount(), e.getOrderDate(), e.getReceivedDate(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy(), Collections.emptyList());
    }
    public PurchaseOrderEntity toEntity(PurchaseOrder d) {
        PurchaseOrderEntity e = new PurchaseOrderEntity();
        e.setId(d.id()); e.setSupplierId(d.supplierId()); e.setBranchId(d.branchId());
        e.setOrderNumber(d.orderNumber()); e.setStatus(d.status()); e.setTotalAmount(d.totalAmount());
        e.setOrderDate(d.orderDate()); e.setReceivedDate(d.receivedDate());
        e.setCreatedBy(d.createdBy()); e.setUpdatedBy(d.updatedBy());
        return e;
    }
    public PurchaseOrderResponse toResponse(PurchaseOrder o) {
        return new PurchaseOrderResponse(o.id(), o.supplierId(), o.branchId(), o.orderNumber(),
                o.status(), o.totalAmount(), o.orderDate(), o.receivedDate(), o.createdBy());
    }
    public PurchaseOrderDetailResponse toDetailResponse(PurchaseOrder o, List<PurchaseOrderDetailResponse.PurchaseItemResponse> items) {
        return new PurchaseOrderDetailResponse(o.id(), o.supplierId(), o.branchId(), o.orderNumber(),
                o.status(), o.totalAmount(), o.orderDate(), o.receivedDate(), o.createdBy(), items);
    }
    public PurchaseOrderDetail detailToDomain(PurchaseOrderDetailEntity e) {
        return new PurchaseOrderDetail(e.getId(), e.getPurchaseOrderId(), e.getProductId(),
                e.getQuantityOrdered(), e.getQuantityReceived(), e.getUnitCost(), e.getSubtotal());
    }
    public PurchaseOrderDetailEntity detailToEntity(PurchaseOrderDetail d) {
        PurchaseOrderDetailEntity e = new PurchaseOrderDetailEntity();
        e.setId(d.id()); e.setPurchaseOrderId(d.purchaseOrderId()); e.setProductId(d.productId());
        e.setQuantityOrdered(d.quantityOrdered()); e.setQuantityReceived(d.quantityReceived());
        e.setUnitCost(d.unitCost()); e.setSubtotal(d.subtotal());
        return e;
    }
    public PurchaseOrderDetailResponse.PurchaseItemResponse detailToResponse(PurchaseOrderDetail d) {
        return new PurchaseOrderDetailResponse.PurchaseItemResponse(d.id(), d.productId(),
                d.quantityOrdered(), d.quantityReceived(), d.unitCost(), d.subtotal());
    }
}
