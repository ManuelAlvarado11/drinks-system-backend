package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.CashRegisterMovement;

import java.math.BigDecimal;
import java.util.List;

public interface CashRegisterMovementRepositoryPort {
    CashRegisterMovement save(CashRegisterMovement movement);
    List<CashRegisterMovement> findByCashRegisterId(Long cashRegisterId);
    BigDecimal sumByTypeAndCashRegisterId(Long cashRegisterId, String type);
}
