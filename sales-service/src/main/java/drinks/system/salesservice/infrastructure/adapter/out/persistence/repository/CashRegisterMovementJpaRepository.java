package drinks.system.salesservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.CashRegisterMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CashRegisterMovementJpaRepository extends JpaRepository<CashRegisterMovementEntity, Long> {

    List<CashRegisterMovementEntity> findByCashRegisterIdOrderByCreatedAtAsc(Long cashRegisterId);

    @Query("SELECT COALESCE(SUM(m.amount), 0) FROM CashRegisterMovementEntity m " +
            "WHERE m.cashRegisterId = :cashRegisterId AND m.movementType = :type")
    BigDecimal sumByTypeAndCashRegisterId(@Param("cashRegisterId") Long cashRegisterId, @Param("type") String type);
}
