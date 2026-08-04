package drinks.system.salesservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.salesservice.application.mapper.SaleMapper;
import drinks.system.salesservice.domain.model.Sale;
import drinks.system.salesservice.domain.port.out.SaleRepositoryPort;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.repository.SaleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SaleRepositoryAdapter implements SaleRepositoryPort {
    private final SaleJpaRepository repo;
    private final SaleMapper mapper;

    @Override
    public Optional<Sale> findById(Long id) { return repo.findById(id).map(mapper::toDomain); }
    @Override
    public Sale save(Sale s) { return mapper.toDomain(repo.save(mapper.toEntity(s))); }
    @Override
    public Page<Sale> findAll(Pageable p, Long branchId, String status, Instant from, Instant to, Long customerId, String paymentMethod) {
        return repo.findAllFiltered(p, branchId, status, from, to, customerId, paymentMethod).map(mapper::toDomain);
    }
    @Override
    public String generateSaleNumber(Long branchId) {
        LocalDate today = LocalDate.now();
        Instant startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        long count = repo.countByBranchAndDate(branchId, startOfDay, endOfDay);
        String dateStr = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        return String.format("VTA-%s-%04d", dateStr, count + 1);
    }
}
