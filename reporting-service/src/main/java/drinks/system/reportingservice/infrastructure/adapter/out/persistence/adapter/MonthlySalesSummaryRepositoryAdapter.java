package drinks.system.reportingservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.reportingservice.application.mapper.ReportingMapper;
import drinks.system.reportingservice.domain.model.MonthlySalesSummary;
import drinks.system.reportingservice.domain.port.out.MonthlySalesSummaryRepositoryPort;
import drinks.system.reportingservice.infrastructure.adapter.out.persistence.repository.MonthlySalesSummaryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository @RequiredArgsConstructor
public class MonthlySalesSummaryRepositoryAdapter implements MonthlySalesSummaryRepositoryPort {
    private final MonthlySalesSummaryJpaRepository repo;
    private final ReportingMapper mapper;
    @Override
    public Page<MonthlySalesSummary> findAll(Pageable p, Long branchId, Integer year) {
        return repo.findAllFiltered(p, branchId, year).map(mapper::monthlyToDomain);
    }
}
