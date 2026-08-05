package drinks.system.reportingservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record MonthlySalesSummary(
        Long id, Long branchId, Integer year, Integer month,
        Integer totalSalesCount, BigDecimal totalRevenue, BigDecimal totalDiscount,
        BigDecimal totalTax, BigDecimal netRevenue, Instant refreshedAt
) {}
