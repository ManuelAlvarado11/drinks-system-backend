package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.SaleDetail;

import java.util.List;

public interface SaleDetailRepositoryPort {
    List<SaleDetail> saveAll(List<SaleDetail> details);
    List<SaleDetail> findBySaleId(Long saleId);
}
