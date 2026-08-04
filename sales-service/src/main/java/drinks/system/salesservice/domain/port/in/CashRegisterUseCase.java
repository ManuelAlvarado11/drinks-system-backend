package drinks.system.salesservice.domain.port.in;

import drinks.system.salesservice.application.dto.request.CloseCashRegisterRequest;
import drinks.system.salesservice.application.dto.request.CreateMovementRequest;
import drinks.system.salesservice.application.dto.request.OpenCashRegisterRequest;
import drinks.system.salesservice.application.dto.response.CashRegisterDetailResponse;
import drinks.system.salesservice.application.dto.response.CashRegisterMovementResponse;
import drinks.system.salesservice.application.dto.response.CashRegisterResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface CashRegisterUseCase {
    CashRegisterResponse open(OpenCashRegisterRequest request, Long userId, Long branchId);
    PageResponse<CashRegisterResponse> findAll(Pageable pageable, Long branchId, String status, Long userId, Instant dateFrom, Instant dateTo);
    CashRegisterDetailResponse findById(Long id);
    CashRegisterResponse findMyOpen(Long userId);
    CashRegisterResponse close(Long id, CloseCashRegisterRequest request, Long userId);
    CashRegisterMovementResponse addMovement(Long id, CreateMovementRequest request, Long userId);
    List<CashRegisterMovementResponse> findMovements(Long id);
}
