package drinks.system.reportingservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.reportingservice.application.mapper.ReportingMapper;
import drinks.system.reportingservice.domain.model.DailySalesSummary;
import drinks.system.reportingservice.domain.port.out.DailySalesSummaryRepositoryPort;
import drinks.system.reportingservice.infrastructure.adapter.out.persistence.repository.DailySalesSummaryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository @RequiredArgsConstructor
public class DailySalesSummaryRepositoryAdapter implements DailySalesSummaryRepositoryPort {
    private final DailySalesSummaryJpaRepository repo;
    private final ReportingMapper mapper;
    @Override
    public Page<DailySalesSummary> findAll(Pageable p, Long branchId, LocalDate from, LocalDate to) {
        return repo.findAllFiltered(p, branchId, from, to).map(mapper::dailyToDomain);
    }
}
