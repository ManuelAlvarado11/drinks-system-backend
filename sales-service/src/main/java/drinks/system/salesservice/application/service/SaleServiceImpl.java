package drinks.system.salesservice.application.service;

import drinks.system.salesservice.application.dto.request.CancelSaleRequest;
import drinks.system.salesservice.application.dto.request.CreateDirectSaleRequest;
import drinks.system.salesservice.application.dto.response.SaleDetailResponse;
import drinks.system.salesservice.application.dto.response.SaleResponse;
import drinks.system.salesservice.application.mapper.SaleMapper;
import drinks.system.salesservice.domain.model.*;
import drinks.system.salesservice.domain.port.in.SaleUseCase;
import drinks.system.salesservice.domain.port.out.*;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.exception.BusinessConflictException;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleUseCase {

    private final SaleRepositoryPort saleRepository;
    private final SaleDetailRepositoryPort saleDetailRepository;
    private final CashRegisterRepositoryPort cashRegisterRepository;
    private final CashRegisterMovementRepositoryPort movementRepository;
    private final InventoryClient inventoryClient;
    private final NameResolverPort nameResolver;
    private final SaleMapper saleMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public SaleResponse createDirect(CreateDirectSaleRequest request, Long userId) {
        // Validate cash register
        cashRegisterRepository.findById(request.cashRegisterId())
                .filter(cr -> "OPEN".equals(cr.status()))
                .orElseThrow(() -> new BusinessConflictException("La caja no está abierta"));

        // Calculate totals
        BigDecimal subtotal = request.items().stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.subtract(discount);

        // Generate sale number and create sale
        String saleNumber = saleRepository.generateSaleNumber(request.branchId());
        Sale sale = new Sale(null, request.branchId(), null, request.customerId(),
                request.cashRegisterId(), saleNumber, subtotal, discount, BigDecimal.ZERO, totalAmount,
                request.paymentMethod(), "COMPLETED", Instant.now(), null, null, userId, null, Collections.emptyList());
        Sale savedSale = saleRepository.save(sale);

        // Create sale details
        List<SaleDetail> details = request.items().stream().map(i -> new SaleDetail(null, savedSale.id(),
                i.productId(), i.quantity(), i.unitPrice(),
                i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())), BigDecimal.ZERO)).toList();
        saleDetailRepository.saveAll(details);

        // Create SALE_INCOME movement
        CashRegisterMovement income = new CashRegisterMovement(null, request.cashRegisterId(),
                "SALE_INCOME", totalAmount, "Venta " + saleNumber, null, userId);
        movementRepository.save(income);

        // Deduct stock (best-effort)
        List<StockDeductionItem> stockItems = request.items().stream()
                .map(i -> new StockDeductionItem(i.productId(), i.quantity())).toList();
        inventoryClient.deductStock(stockItems, request.branchId());

        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "SALES",
                "Sale", savedSale.id(), null, null, null, "Venta directa: " + saleNumber));

        return saleMapper.toResponse(savedSale);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SaleResponse> findAll(Pageable pageable, Long branchId, String status,
                                               Instant dateFrom, Instant dateTo, Long customerId, String paymentMethod) {
        Page<Sale> page = saleRepository.findAll(pageable, branchId, status, dateFrom, dateTo, customerId, paymentMethod);
        List<SaleResponse> content = page.getContent().stream().map(saleMapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleDetailResponse findById(Long id) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venta", id));
        List<SaleDetail> details = saleDetailRepository.findBySaleId(id);

        // Resolve product names
        java.util.Set<Long> productIds = details.stream().map(SaleDetail::productId).collect(java.util.stream.Collectors.toSet());
        java.util.Map<Long, String> productNames = nameResolver.findProductNamesByIds(productIds);

        List<SaleDetailResponse.SaleItemResponse> items = details.stream()
                .map(d -> saleMapper.detailToResponse(d, productNames.getOrDefault(d.productId(), "Producto #" + d.productId())))
                .toList();
        return saleMapper.toDetailResponse(sale, items);
    }

    @Override
    @Transactional
    public void cancel(Long id, CancelSaleRequest request, Long userId) {
        Sale sale = saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venta", id));
        if ("CANCELLED".equals(sale.status())) {
            throw new BusinessConflictException("La venta ya está cancelada");
        }

        Sale cancelled = new Sale(sale.id(), sale.branchId(), sale.accountId(), sale.customerId(),
                sale.cashRegisterId(), sale.saleNumber(), sale.subtotal(), sale.discountAmount(),
                sale.taxAmount(), sale.totalAmount(), sale.paymentMethod(), "CANCELLED",
                sale.saleDate(), sale.createdAt(), sale.updatedAt(), sale.createdBy(), userId, Collections.emptyList());
        saleRepository.save(cancelled);

        // Revert stock (best-effort)
        List<SaleDetail> details = saleDetailRepository.findBySaleId(id);
        List<StockDeductionItem> stockItems = details.stream()
                .map(d -> new StockDeductionItem(d.productId(), d.quantity())).toList();
        inventoryClient.addStock(stockItems, sale.branchId());

        eventPublisher.publishEvent(new AuditEvent(userId, null, "UPDATE", "SALES",
                "Sale", id, null, null, null, "Venta cancelada: " + sale.saleNumber() + ". Motivo: " + request.reason()));
    }
}
