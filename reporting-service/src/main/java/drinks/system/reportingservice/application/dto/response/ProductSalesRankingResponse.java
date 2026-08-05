package drinks.system.reportingservice.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductSalesRankingResponse(
        Long id, Long productId, Long branchId, String productName,
        String categoryName, Integer totalQuantitySold, BigDecimal totalRevenue,
        BigDecimal profit, LocalDate periodStart, LocalDate periodEnd
) {}
