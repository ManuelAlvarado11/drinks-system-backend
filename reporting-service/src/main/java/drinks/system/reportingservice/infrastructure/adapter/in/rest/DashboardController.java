package drinks.system.reportingservice.infrastructure.adapter.in.rest;

import drinks.system.reportingservice.application.dto.response.*;
import drinks.system.reportingservice.domain.port.in.DashboardUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.security.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reporting/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardUseCase dashboardUseCase;

    @GetMapping("/daily-sales")
    @RequiresPermission("REPORTING_READ")
    public ResponseEntity<ApiResponse<PageResponse<DailySalesSummaryResponse>>> getDailySales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "summaryDate"));
        return ResponseEntity.ok(ApiResponse.success(dashboardUseCase.getDailySales(pageable, branchId, dateFrom, dateTo)));
    }

    @GetMapping("/monthly-sales")
    @RequiresPermission("REPORTING_READ")
    public ResponseEntity<ApiResponse<PageResponse<MonthlySalesSummaryResponse>>> getMonthlySales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Integer year) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "year", "month"));
        return ResponseEntity.ok(ApiResponse.success(dashboardUseCase.getMonthlySales(pageable, branchId, year)));
    }

    @GetMapping("/product-ranking")
    @RequiresPermission("REPORTING_READ")
    public ResponseEntity<ApiResponse<PageResponse<ProductSalesRankingResponse>>> getProductRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) LocalDate periodStart,
            @RequestParam(required = false) LocalDate periodEnd) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "totalRevenue"));
        return ResponseEntity.ok(ApiResponse.success(dashboardUseCase.getProductRanking(pageable, branchId, periodStart, periodEnd)));
    }

    @GetMapping("/inventory-status")
    @RequiresPermission("REPORTING_READ")
    public ResponseEntity<ApiResponse<PageResponse<InventoryStatusResponse>>> getInventoryStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Boolean lowStockOnly) {
        var pageable = PageRequest.of(page, Math.min(size, 100), Sort.by("productName"));
        return ResponseEntity.ok(ApiResponse.success(dashboardUseCase.getInventoryStatus(pageable, branchId, lowStockOnly)));
    }

    /**
     * Returns sales broken down by category and day (or month).
     *
     * Query params:
     *   branchId    – optional branch filter
     *   dateFrom    – optional start date (inclusive, yyyy-MM-dd)
     *   dateTo      – optional end date   (inclusive, yyyy-MM-dd)
     *   categoryIds – optional repeatable param, e.g. ?categoryIds=3&categoryIds=7
     *   groupBy     – "day" (default) | "month"
     */
    @GetMapping("/sales-by-category")
    @RequiresPermission("REPORTING_READ")
    public ResponseEntity<ApiResponse<PageResponse<SalesByCategoryResponse>>> getSalesByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(defaultValue = "day") String groupBy) {
        var pageable = PageRequest.of(page, Math.min(size, 200));
        return ResponseEntity.ok(ApiResponse.success(
                dashboardUseCase.getSalesByCategory(pageable, branchId, dateFrom, dateTo, categoryIds, groupBy)));
    }
}
