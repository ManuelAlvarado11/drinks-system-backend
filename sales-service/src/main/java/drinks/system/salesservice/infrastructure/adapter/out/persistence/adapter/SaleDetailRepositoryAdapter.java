package drinks.system.salesservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.salesservice.application.mapper.SaleMapper;
import drinks.system.salesservice.domain.model.SaleDetail;
import drinks.system.salesservice.domain.port.out.SaleDetailRepositoryPort;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.repository.SaleDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SaleDetailRepositoryAdapter implements SaleDetailRepositoryPort {
    private final SaleDetailJpaRepository repo;
    private final SaleMapper mapper;

    @Override
    public List<SaleDetail> saveAll(List<SaleDetail> details) {
        var entities = details.stream().map(mapper::detailToEntity).toList();
        return repo.saveAll(entities).stream().map(mapper::detailToDomain).toList();
    }
    @Override
    public List<SaleDetail> findBySaleId(Long saleId) {
        return repo.findBySaleId(saleId).stream().map(mapper::detailToDomain).toList();
    }
}
