package drinks.system.salesservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.SaleDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleDetailJpaRepository extends JpaRepository<SaleDetailEntity, Long> {

    List<SaleDetailEntity> findBySaleId(Long saleId);
}
