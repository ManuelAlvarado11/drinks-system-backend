package drinks.system.reportingservice.domain.port.in;

import drinks.system.reportingservice.application.dto.response.*;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface DashboardUseCase {
    PageResponse<DailySalesSummaryResponse> getDailySales(Pageable pageable, Long branchId, LocalDate dateFrom, LocalDate dateTo);
    PageResponse<MonthlySalesSummaryResponse> getMonthlySales(Pageable pageable, Long branchId, Integer year);
    PageResponse<ProductSalesRankingResponse> getProductRanking(Pageable pageable, Long branchId, LocalDate periodStart, LocalDate periodEnd);
    PageResponse<InventoryStatusResponse> getInventoryStatus(Pageable pageable, Long branchId, Boolean lowStockOnly);
}
