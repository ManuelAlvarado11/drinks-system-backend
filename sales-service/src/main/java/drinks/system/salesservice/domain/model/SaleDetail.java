package drinks.system.salesservice.domain.model;

import java.math.BigDecimal;

public record SaleDetail(
        Long id,
        Long saleId,
        Long productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        BigDecimal discount
) {
}
