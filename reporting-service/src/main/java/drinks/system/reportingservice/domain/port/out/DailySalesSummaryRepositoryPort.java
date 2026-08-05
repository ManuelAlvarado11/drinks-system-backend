package drinks.system.reportingservice.domain.port.out;

import drinks.system.reportingservice.domain.model.DailySalesSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface DailySalesSummaryRepositoryPort {
    Page<DailySalesSummary> findAll(Pageable pageable, Long branchId, LocalDate dateFrom, LocalDate dateTo);
}
