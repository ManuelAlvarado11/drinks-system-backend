package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerRepositoryPort {
    Optional<Customer> findById(Long id);
    Customer save(Customer customer);
    Page<Customer> findAll(Pageable pageable, String search);
    boolean existsByNitCi(String nitCi);
}
