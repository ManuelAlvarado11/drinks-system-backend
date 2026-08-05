package drinks.system.reportingservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record DailySalesSummary(
        Long id, Long branchId, LocalDate summaryDate,
        Integer totalSalesCount, BigDecimal totalRevenue, BigDecimal totalDiscount,
        BigDecimal totalTax, BigDecimal netRevenue, Instant refreshedAt
) {}
