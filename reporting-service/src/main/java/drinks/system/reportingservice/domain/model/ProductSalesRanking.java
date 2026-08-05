package drinks.system.reportingservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ProductSalesRanking(
        Long id, Long productId, Long branchId, String productName,
        String categoryName, Integer totalQuantitySold, BigDecimal totalRevenue,
        BigDecimal profit, LocalDate periodStart, LocalDate periodEnd, Instant refreshedAt
) {}
