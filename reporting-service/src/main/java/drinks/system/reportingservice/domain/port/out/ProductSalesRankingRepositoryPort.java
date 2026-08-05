package drinks.system.reportingservice.domain.port.out;

import drinks.system.reportingservice.domain.model.ProductSalesRanking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface ProductSalesRankingRepositoryPort {
    Page<ProductSalesRanking> findAll(Pageable pageable, Long branchId, LocalDate periodStart, LocalDate periodEnd);
}
