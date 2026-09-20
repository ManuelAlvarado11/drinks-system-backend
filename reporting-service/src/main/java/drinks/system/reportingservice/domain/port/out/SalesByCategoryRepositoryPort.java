package drinks.system.reportingservice.domain.port.out;

import drinks.system.reportingservice.application.dto.response.SalesByCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SalesByCategoryRepositoryPort {

    /**
     * Queries sales.sales → sale_details → products → categories directly,
     * grouping results by category and day (or month when groupBy="month").
     *
     * @param categoryIds empty/null means all categories
     * @param groupBy     "day" (default) or "month"
     */
    Page<SalesByCategoryResponse> findSalesByCategory(
            Pageable pageable,
            Long branchId,
            LocalDate dateFrom,
            LocalDate dateTo,
            List<Long> categoryIds,
            String groupBy);
}
