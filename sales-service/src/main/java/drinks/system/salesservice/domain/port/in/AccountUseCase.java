package drinks.system.salesservice.domain.port.in;

import drinks.system.salesservice.application.dto.request.AddAccountItemRequest;
import drinks.system.salesservice.application.dto.request.CloseAccountRequest;
import drinks.system.salesservice.application.dto.request.OpenAccountRequest;
import drinks.system.salesservice.application.dto.response.AccountDetailResponse;
import drinks.system.salesservice.application.dto.response.AccountItemResponse;
import drinks.system.salesservice.application.dto.response.AccountResponse;
import drinks.system.salesservice.application.dto.response.SaleResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface AccountUseCase {
    AccountResponse open(OpenAccountRequest request, Long userId);
    PageResponse<AccountResponse> findAll(Pageable pageable, Long branchId, String status, Instant dateFrom, Instant dateTo);
    AccountDetailResponse findById(Long id);
    AccountItemResponse addItem(Long accountId, AddAccountItemRequest request, Long userId);
    void cancelItem(Long accountId, Long detailId);
    SaleResponse close(Long accountId, CloseAccountRequest request, Long userId);
    void cancel(Long accountId);
}
