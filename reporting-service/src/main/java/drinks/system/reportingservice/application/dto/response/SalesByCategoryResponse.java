package drinks.system.reportingservice.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesByCategoryResponse(
        Long categoryId,
        String categoryName,
        LocalDate periodDate,
        Integer totalSalesCount,
        BigDecimal totalRevenue,
        BigDecimal totalDiscount,
        BigDecimal netRevenue
) {}
