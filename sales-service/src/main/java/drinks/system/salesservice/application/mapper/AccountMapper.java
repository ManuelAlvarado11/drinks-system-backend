package drinks.system.salesservice.application.mapper;

import drinks.system.salesservice.application.dto.response.AccountDetailResponse;
import drinks.system.salesservice.application.dto.response.AccountItemResponse;
import drinks.system.salesservice.application.dto.response.AccountResponse;
import drinks.system.salesservice.domain.model.Account;
import drinks.system.salesservice.domain.model.AccountDetail;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.AccountDetailEntity;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
public class AccountMapper {

    public Account toDomain(AccountEntity e) {
        return new Account(e.getId(), e.getBranchId(), e.getCustomerName(), e.getCustomerLastName(),
                e.getTableNumber(), e.getInternalCode(), e.getStatus(),
                e.getOpenedAt(), e.getClosedAt(), e.getOpenedBy(), e.getClosedBy(),
                e.getNotes(), e.getCreatedAt(), e.getUpdatedAt(), Collections.emptyList());
    }

    public AccountEntity toEntity(Account d) {
        AccountEntity e = new AccountEntity();
        e.setId(d.id());
        e.setBranchId(d.branchId());
        e.setCustomerName(d.customerName());
        e.setCustomerLastName(d.customerLastName());
        e.setTableNumber(d.tableNumber());
        e.setInternalCode(d.internalCode());
        e.setStatus(d.status());
        e.setOpenedAt(d.openedAt());
        e.setClosedAt(d.closedAt());
        e.setOpenedBy(d.openedBy());
        e.setClosedBy(d.closedBy());
        e.setNotes(d.notes());
        return e;
    }

    public AccountResponse toResponse(Account a, BigDecimal total) {
        return new AccountResponse(a.id(), a.branchId(), a.customerName(), a.customerLastName(),
                a.tableNumber(), a.internalCode(), a.status(), a.openedAt(), a.closedAt(),
                a.openedBy(), a.closedBy(), a.notes(), total);
    }

    public AccountDetailResponse toDetailResponse(Account a, BigDecimal total, List<AccountItemResponse> items) {
        return new AccountDetailResponse(a.id(), a.branchId(), a.customerName(), a.customerLastName(),
                a.tableNumber(), a.internalCode(), a.status(), a.openedAt(), a.closedAt(),
                a.openedBy(), a.closedBy(), a.notes(), total, items);
    }

    public AccountDetail detailToDomain(AccountDetailEntity e) {
        return new AccountDetail(e.getId(), e.getAccountId(), e.getProductId(),
                e.getQuantity(), e.getUnitPrice(), e.getSubtotal(),
                e.getAddedAt(), e.getAddedBy(), e.getIsCancelled());
    }

    public AccountDetailEntity detailToEntity(AccountDetail d) {
        AccountDetailEntity e = new AccountDetailEntity();
        e.setId(d.id());
        e.setAccountId(d.accountId());
        e.setProductId(d.productId());
        e.setQuantity(d.quantity());
        e.setUnitPrice(d.unitPrice());
        e.setSubtotal(d.subtotal());
        e.setAddedAt(d.addedAt());
        e.setAddedBy(d.addedBy());
        e.setIsCancelled(d.isCancelled());
        return e;
    }

    public AccountItemResponse detailToResponse(AccountDetail d) {
        return new AccountItemResponse(d.id(), d.productId(), null, d.quantity(),
                d.unitPrice(), d.subtotal(), d.addedAt(), d.addedBy(), d.isCancelled());
    }

    public AccountItemResponse detailToResponse(AccountDetail d, String productName) {
        return new AccountItemResponse(d.id(), d.productId(), productName, d.quantity(),
                d.unitPrice(), d.subtotal(), d.addedAt(), d.addedBy(), d.isCancelled());
    }
}
