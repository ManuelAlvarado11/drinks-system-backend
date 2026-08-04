package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface SaleRepositoryPort {
    Optional<Sale> findById(Long id);
    Sale save(Sale sale);
    Page<Sale> findAll(Pageable pageable, Long branchId, String status, Instant dateFrom, Instant dateTo, Long customerId, String paymentMethod);
    String generateSaleNumber(Long branchId);
}
