package drinks.system.salesservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.BranchPrinterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BranchPrinterJpaRepository extends JpaRepository<BranchPrinterEntity, Long> {

    List<BranchPrinterEntity> findByBranchIdOrderByIsDefaultDescNameAsc(Long branchId);

    Optional<BranchPrinterEntity> findFirstByBranchIdAndIsDefaultTrueAndIsActiveTrue(Long branchId);

    @Modifying
    @Query("UPDATE BranchPrinterEntity p SET p.isDefault = false WHERE p.branchId = :branchId")
    void clearDefaultForBranch(@Param("branchId") Long branchId);
}
