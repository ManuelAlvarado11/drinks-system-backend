package drinks.system.reportingservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record MonthlySalesSummaryResponse(
        Long id, Long branchId, Integer year, Integer month,
        Integer totalSalesCount, BigDecimal totalRevenue, BigDecimal totalDiscount,
        BigDecimal totalTax, BigDecimal netRevenue, Instant refreshedAt
) {}
