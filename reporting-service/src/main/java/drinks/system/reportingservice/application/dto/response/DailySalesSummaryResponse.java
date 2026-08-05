package drinks.system.reportingservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record DailySalesSummaryResponse(
        Long id, Long branchId, LocalDate summaryDate,
        Integer totalSalesCount, BigDecimal totalRevenue, BigDecimal totalDiscount,
        BigDecimal totalTax, BigDecimal netRevenue, Instant refreshedAt
) {}
