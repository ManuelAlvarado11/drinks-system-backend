package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.CashRegister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface CashRegisterRepositoryPort {
    Optional<CashRegister> findById(Long id);
    CashRegister save(CashRegister cashRegister);
    Page<CashRegister> findAll(Pageable pageable, Long branchId, String status, Long userId, Instant dateFrom, Instant dateTo);
    Optional<CashRegister> findOpenByUserIdAndBranchId(Long userId, Long branchId);
    Optional<CashRegister> findOpenByUserId(Long userId);
}
