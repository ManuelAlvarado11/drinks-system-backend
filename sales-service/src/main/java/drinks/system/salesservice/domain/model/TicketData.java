package drinks.system.salesservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * All data needed to render a sale receipt (ticket), assembled by the
 * application layer from the Sale, its details and branch/config lookups.
 */
public record TicketData(
        String header,
        String footer,
        String branchName,
        String branchAddress,
        String branchPhone,
        String saleNumber,
        Instant saleDate,
        String paymentMethod,
        String cashierName,
        String customerName,
        String tableNumber,
        List<Line> items,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        boolean reprint
) {
    public record Line(
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {}
}
