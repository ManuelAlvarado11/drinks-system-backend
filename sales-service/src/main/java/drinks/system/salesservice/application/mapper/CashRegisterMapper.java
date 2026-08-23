package drinks.system.salesservice.application.mapper;

import drinks.system.salesservice.application.dto.response.CashRegisterDetailResponse;
import drinks.system.salesservice.application.dto.response.CashRegisterMovementResponse;
import drinks.system.salesservice.application.dto.response.CashRegisterResponse;
import drinks.system.salesservice.domain.model.CashRegister;
import drinks.system.salesservice.domain.model.CashRegisterMovement;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.CashRegisterEntity;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.CashRegisterMovementEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CashRegisterMapper {

    public CashRegister toDomain(CashRegisterEntity e) {
        return new CashRegister(e.getId(), e.getBranchId(), e.getUserId(),
                e.getOpeningAmount(), e.getClosingAmount(), e.getExpectedAmount(),
                e.getDifference(), e.getStatus(), e.getOpenedAt(), e.getClosedAt(),
                e.getNotes(), e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy());
    }

    public CashRegisterEntity toEntity(CashRegister d) {
        CashRegisterEntity e = new CashRegisterEntity();
        e.setId(d.id());
        e.setBranchId(d.branchId());
        e.setUserId(d.userId());
        e.setOpeningAmount(d.openingAmount());
        e.setClosingAmount(d.closingAmount());
        e.setExpectedAmount(d.expectedAmount());
        e.setDifference(d.difference());
        e.setStatus(d.status());
        e.setOpenedAt(d.openedAt());
        e.setClosedAt(d.closedAt());
        e.setNotes(d.notes());
        e.setCreatedBy(d.createdBy());
        e.setUpdatedBy(d.updatedBy());
        return e;
    }

    public CashRegisterResponse toResponse(CashRegister d) {
        return new CashRegisterResponse(d.id(), d.branchId(), d.userId(), null,
                d.openingAmount(), d.closingAmount(), d.expectedAmount(),
                d.difference(), d.status(), d.openedAt(), d.closedAt(), d.notes());
    }

    public CashRegisterResponse toResponse(CashRegister d, String username) {
        return new CashRegisterResponse(d.id(), d.branchId(), d.userId(), username,
                d.openingAmount(), d.closingAmount(), d.expectedAmount(),
                d.difference(), d.status(), d.openedAt(), d.closedAt(), d.notes());
    }

    public CashRegisterDetailResponse toDetailResponse(CashRegister d, List<CashRegisterMovementResponse> movements) {
        return new CashRegisterDetailResponse(d.id(), d.branchId(), d.userId(),
                d.openingAmount(), d.closingAmount(), d.expectedAmount(),
                d.difference(), d.status(), d.openedAt(), d.closedAt(), d.notes(), movements);
    }

    public CashRegisterMovement movementToDomain(CashRegisterMovementEntity e) {
        return new CashRegisterMovement(e.getId(), e.getCashRegisterId(), e.getMovementType(),
                e.getAmount(), e.getDescription(), e.getCreatedAt(), e.getCreatedBy());
    }

    public CashRegisterMovementEntity movementToEntity(CashRegisterMovement d) {
        CashRegisterMovementEntity e = new CashRegisterMovementEntity();
        e.setId(d.id());
        e.setCashRegisterId(d.cashRegisterId());
        e.setMovementType(d.movementType());
        e.setAmount(d.amount());
        e.setDescription(d.description());
        e.setCreatedBy(d.createdBy());
        return e;
    }

    public CashRegisterMovementResponse movementToResponse(CashRegisterMovement d) {
        return new CashRegisterMovementResponse(d.id(), d.cashRegisterId(), d.movementType(),
                d.amount(), d.description(), d.createdAt(), d.createdBy());
    }
}
