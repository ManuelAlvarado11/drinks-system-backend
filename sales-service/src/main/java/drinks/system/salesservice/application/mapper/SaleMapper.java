package drinks.system.salesservice.application.mapper;

import drinks.system.salesservice.application.dto.response.SaleDetailResponse;
import drinks.system.salesservice.application.dto.response.SaleResponse;
import drinks.system.salesservice.domain.model.Sale;
import drinks.system.salesservice.domain.model.SaleDetail;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.SaleDetailEntity;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.SaleEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SaleMapper {

    public Sale toDomain(SaleEntity e) {
        return new Sale(e.getId(), e.getBranchId(), e.getAccountId(), e.getCustomerId(),
                e.getCashRegisterId(), e.getSaleNumber(), e.getSubtotal(), e.getDiscountAmount(),
                e.getTaxAmount(), e.getTotalAmount(), e.getPaymentMethod(), e.getStatus(),
                e.getSaleDate(), e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(),
                e.getUpdatedBy(), Collections.emptyList());
    }

    public SaleEntity toEntity(Sale d) {
        SaleEntity e = new SaleEntity();
        e.setId(d.id());
        e.setBranchId(d.branchId());
        e.setAccountId(d.accountId());
        e.setCustomerId(d.customerId());
        e.setCashRegisterId(d.cashRegisterId());
        e.setSaleNumber(d.saleNumber());
        e.setSubtotal(d.subtotal());
        e.setDiscountAmount(d.discountAmount());
        e.setTaxAmount(d.taxAmount());
        e.setTotalAmount(d.totalAmount());
        e.setPaymentMethod(d.paymentMethod());
        e.setStatus(d.status());
        e.setSaleDate(d.saleDate());
        e.setCreatedBy(d.createdBy());
        e.setUpdatedBy(d.updatedBy());
        return e;
    }

    public SaleResponse toResponse(Sale s) {
        return new SaleResponse(s.id(), s.branchId(), s.accountId(), s.customerId(),
                s.cashRegisterId(), s.saleNumber(), s.subtotal(), s.discountAmount(),
                s.taxAmount(), s.totalAmount(), s.paymentMethod(), s.status(),
                s.saleDate(), s.createdBy());
    }

    public SaleDetailResponse toDetailResponse(Sale s, List<SaleDetailResponse.SaleItemResponse> items) {
        return new SaleDetailResponse(s.id(), s.branchId(), s.accountId(), s.customerId(),
                s.cashRegisterId(), s.saleNumber(), s.subtotal(), s.discountAmount(),
                s.taxAmount(), s.totalAmount(), s.paymentMethod(), s.status(),
                s.saleDate(), s.createdBy(), items);
    }

    public SaleDetail detailToDomain(SaleDetailEntity e) {
        return new SaleDetail(e.getId(), e.getSaleId(), e.getProductId(),
                e.getQuantity(), e.getUnitPrice(), e.getSubtotal(), e.getDiscount());
    }

    public SaleDetailEntity detailToEntity(SaleDetail d) {
        SaleDetailEntity e = new SaleDetailEntity();
        e.setId(d.id());
        e.setSaleId(d.saleId());
        e.setProductId(d.productId());
        e.setQuantity(d.quantity());
        e.setUnitPrice(d.unitPrice());
        e.setSubtotal(d.subtotal());
        e.setDiscount(d.discount());
        return e;
    }

    public SaleDetailResponse.SaleItemResponse detailToResponse(SaleDetail d) {
        return new SaleDetailResponse.SaleItemResponse(d.id(), d.productId(), null,
                d.quantity(), d.unitPrice(), d.subtotal(), d.discount());
    }

    public SaleDetailResponse.SaleItemResponse detailToResponse(SaleDetail d, String productName) {
        return new SaleDetailResponse.SaleItemResponse(d.id(), d.productId(), productName,
                d.quantity(), d.unitPrice(), d.subtotal(), d.discount());
    }
}
