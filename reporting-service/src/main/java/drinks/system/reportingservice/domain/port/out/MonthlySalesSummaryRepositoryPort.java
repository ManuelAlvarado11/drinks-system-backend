package drinks.system.reportingservice.domain.port.out;

import drinks.system.reportingservice.domain.model.MonthlySalesSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MonthlySalesSummaryRepositoryPort {
    Page<MonthlySalesSummary> findAll(Pageable pageable, Long branchId, Integer year);
}
