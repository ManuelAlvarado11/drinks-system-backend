package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.StockDeductionItem;

import java.util.List;

public interface InventoryClient {
    void deductStock(List<StockDeductionItem> items, Long branchId);
    void addStock(List<StockDeductionItem> items, Long branchId);
}
