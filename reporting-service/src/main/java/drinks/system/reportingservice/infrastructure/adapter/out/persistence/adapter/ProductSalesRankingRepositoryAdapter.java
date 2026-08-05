package drinks.system.reportingservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.reportingservice.application.mapper.ReportingMapper;
import drinks.system.reportingservice.domain.model.ProductSalesRanking;
import drinks.system.reportingservice.domain.port.out.ProductSalesRankingRepositoryPort;
import drinks.system.reportingservice.infrastructure.adapter.out.persistence.repository.ProductSalesRankingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository @RequiredArgsConstructor
public class ProductSalesRankingRepositoryAdapter implements ProductSalesRankingRepositoryPort {
    private final ProductSalesRankingJpaRepository repo;
    private final ReportingMapper mapper;
    @Override
    public Page<ProductSalesRanking> findAll(Pageable p, Long branchId, LocalDate start, LocalDate end) {
        return repo.findAllFiltered(p, branchId, start, end).map(mapper::rankingToDomain);
    }
}
