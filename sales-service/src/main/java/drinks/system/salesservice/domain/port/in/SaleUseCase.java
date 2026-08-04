package drinks.system.salesservice.domain.port.in;

import drinks.system.salesservice.application.dto.request.CancelSaleRequest;
import drinks.system.salesservice.application.dto.request.CreateDirectSaleRequest;
import drinks.system.salesservice.application.dto.response.SaleDetailResponse;
import drinks.system.salesservice.application.dto.response.SaleResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface SaleUseCase {
    SaleResponse createDirect(CreateDirectSaleRequest request, Long userId);
    PageResponse<SaleResponse> findAll(Pageable pageable, Long branchId, String status, Instant dateFrom, Instant dateTo, Long customerId, String paymentMethod);
    SaleDetailResponse findById(Long id);
    void cancel(Long id, CancelSaleRequest request, Long userId);
}
