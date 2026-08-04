package drinks.system.salesservice.domain.port.in;

import drinks.system.salesservice.application.dto.request.CreateCustomerRequest;
import drinks.system.salesservice.application.dto.request.UpdateCustomerRequest;
import drinks.system.salesservice.application.dto.response.CustomerResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CustomerUseCase {
    CustomerResponse create(CreateCustomerRequest request, Long userId);
    PageResponse<CustomerResponse> findAll(Pageable pageable, String search);
    CustomerResponse findById(Long id);
    CustomerResponse update(Long id, UpdateCustomerRequest request, Long userId);
    void delete(Long id);
}
