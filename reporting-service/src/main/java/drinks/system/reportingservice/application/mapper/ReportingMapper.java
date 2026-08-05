package drinks.system.reportingservice.application.mapper;

import drinks.system.reportingservice.application.dto.response.*;
import drinks.system.reportingservice.domain.model.*;
import drinks.system.reportingservice.infrastructure.adapter.out.persistence.entity.*;
import org.springframework.stereotype.Component;

@Component
public class ReportingMapper {

    public DailySalesSummary dailyToDomain(DailySalesSummaryEntity e) {
        return new DailySalesSummary(e.getId(), e.getBranchId(), e.getSummaryDate(),
                e.getTotalSalesCount(), e.getTotalRevenue(), e.getTotalDiscount(),
                e.getTotalTax(), e.getNetRevenue(), e.getRefreshedAt());
    }

    public DailySalesSummaryResponse dailyToResponse(DailySalesSummary d) {
        return new DailySalesSummaryResponse(d.id(), d.branchId(), d.summaryDate(),
                d.totalSalesCount(), d.totalRevenue(), d.totalDiscount(),
                d.totalTax(), d.netRevenue(), d.refreshedAt());
    }

    public MonthlySalesSummary monthlyToDomain(MonthlySalesSummaryEntity e) {
        return new MonthlySalesSummary(e.getId(), e.getBranchId(), e.getYear(), e.getMonth(),
                e.getTotalSalesCount(), e.getTotalRevenue(), e.getTotalDiscount(),
                e.getTotalTax(), e.getNetRevenue(), e.getRefreshedAt());
    }

    public MonthlySalesSummaryResponse monthlyToResponse(MonthlySalesSummary d) {
        return new MonthlySalesSummaryResponse(d.id(), d.branchId(), d.year(), d.month(),
                d.totalSalesCount(), d.totalRevenue(), d.totalDiscount(),
                d.totalTax(), d.netRevenue(), d.refreshedAt());
    }

    public ProductSalesRanking rankingToDomain(ProductSalesRankingEntity e) {
        return new ProductSalesRanking(e.getId(), e.getProductId(), e.getBranchId(),
                e.getProductName(), e.getCategoryName(), e.getTotalQuantitySold(),
                e.getTotalRevenue(), e.getProfit(), e.getPeriodStart(), e.getPeriodEnd(), e.getRefreshedAt());
    }

    public ProductSalesRankingResponse rankingToResponse(ProductSalesRanking d) {
        return new ProductSalesRankingResponse(d.id(), d.productId(), d.branchId(),
                d.productName(), d.categoryName(), d.totalQuantitySold(),
                d.totalRevenue(), d.profit(), d.periodStart(), d.periodEnd());
    }

    public InventoryStatus inventoryToDomain(InventoryStatusEntity e) {
        return new InventoryStatus(e.getId(), e.getProductId(), e.getBranchId(),
                e.getProductName(), e.getCategoryName(), e.getCurrentStock(), e.getMinimumStock(),
                e.getCostPrice(), e.getSalePrice(), e.getIsLowStock(), e.getRefreshedAt());
    }

    public InventoryStatusResponse inventoryToResponse(InventoryStatus d) {
        return new InventoryStatusResponse(d.id(), d.productId(), d.branchId(),
                d.productName(), d.categoryName(), d.currentStock(), d.minimumStock(),
                d.costPrice(), d.salePrice(), d.isLowStock());
    }
}
