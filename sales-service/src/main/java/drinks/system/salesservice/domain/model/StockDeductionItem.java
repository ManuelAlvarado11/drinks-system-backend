package drinks.system.salesservice.domain.model;

public record StockDeductionItem(
        Long productId,
        Integer quantity
) {
}
