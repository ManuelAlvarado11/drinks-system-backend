package drinks.system.reportingservice.application.service;

import drinks.system.reportingservice.application.dto.response.*;
import drinks.system.reportingservice.application.mapper.ReportingMapper;
import drinks.system.reportingservice.domain.model.*;
import drinks.system.reportingservice.domain.port.in.DashboardUseCase;
import drinks.system.reportingservice.domain.port.out.*;
import drinks.system.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service @RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardUseCase {
    private final DailySalesSummaryRepositoryPort dailyRepo;
    private final MonthlySalesSummaryRepositoryPort monthlyRepo;
    private final ProductSalesRankingRepositoryPort rankingRepo;
    private final InventoryStatusRepositoryPort inventoryRepo;
    private final ReportingMapper mapper;

    @Override @Transactional(readOnly = true)
    public PageResponse<DailySalesSummaryResponse> getDailySales(Pageable p, Long branchId, LocalDate from, LocalDate to) {
        Page<DailySalesSummary> page = dailyRepo.findAll(p, branchId, from, to);
        List<DailySalesSummaryResponse> content = page.getContent().stream().map(mapper::dailyToResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<MonthlySalesSummaryResponse> getMonthlySales(Pageable p, Long branchId, Integer year) {
        Page<MonthlySalesSummary> page = monthlyRepo.findAll(p, branchId, year);
        List<MonthlySalesSummaryResponse> content = page.getContent().stream().map(mapper::monthlyToResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<ProductSalesRankingResponse> getProductRanking(Pageable p, Long branchId, LocalDate start, LocalDate end) {
        Page<ProductSalesRanking> page = rankingRepo.findAll(p, branchId, start, end);
        List<ProductSalesRankingResponse> content = page.getContent().stream().map(mapper::rankingToResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<InventoryStatusResponse> getInventoryStatus(Pageable p, Long branchId, Boolean lowStockOnly) {
        Page<InventoryStatus> page = inventoryRepo.findAll(p, branchId, lowStockOnly);
        List<InventoryStatusResponse> content = page.getContent().stream().map(mapper::inventoryToResponse).toList();
        return PageResponse.of(page, content);
    }
}
