package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @param lineTotal optional exact total for the whole line (e.g. a "Balde" that
 *                  costs exactly $8 for 6 units). When present it is used as the
 *                  subtotal verbatim, avoiding the rounding error of
 *                  unitPrice × quantity. When null the subtotal is computed as
 *                  unitPrice × quantity.
 */
public record AddAccountItemRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity,
        @NotNull BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
