package drinks.system.salesservice.application.service;

import drinks.system.salesservice.application.dto.request.AddAccountItemRequest;
import drinks.system.salesservice.application.dto.request.CloseAccountRequest;
import drinks.system.salesservice.application.dto.request.OpenAccountRequest;
import drinks.system.salesservice.application.dto.response.*;
import drinks.system.salesservice.application.mapper.AccountMapper;
import drinks.system.salesservice.application.mapper.SaleMapper;
import drinks.system.salesservice.domain.model.*;
import drinks.system.salesservice.domain.port.in.AccountUseCase;
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
public class AccountServiceImpl implements AccountUseCase {

    private final AccountRepositoryPort accountRepository;
    private final AccountDetailRepositoryPort detailRepository;
    private final SaleRepositoryPort saleRepository;
    private final SaleDetailRepositoryPort saleDetailRepository;
    private final CashRegisterMovementRepositoryPort movementRepository;
    private final CashRegisterRepositoryPort cashRegisterRepository;
    private final InventoryClient inventoryClient;
    private final AccountMapper accountMapper;
    private final SaleMapper saleMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public AccountResponse open(OpenAccountRequest request, Long userId) {
        Account account = new Account(null, request.branchId(), request.customerName(),
                request.customerLastName(), request.tableNumber(), request.internalCode(),
                "OPEN", null, null, userId, null, request.notes(), null, null, Collections.emptyList());
        Account saved = accountRepository.save(account);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "SALES",
                "Account", saved.id(), null, null, null, "Cuenta abierta mesa: " + request.tableNumber()));
        return accountMapper.toResponse(saved, BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AccountResponse> findAll(Pageable pageable, Long branchId, String status, Instant dateFrom, Instant dateTo) {
        Page<Account> page = accountRepository.findAll(pageable, branchId, status, dateFrom, dateTo);
        List<AccountResponse> content = page.getContent().stream().map(a -> {
            BigDecimal total = calculateTotal(a.id());
            return accountMapper.toResponse(a, total);
        }).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDetailResponse findById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cuenta", id));
        List<AccountDetail> details = detailRepository.findByAccountId(id);
        BigDecimal total = details.stream().filter(d -> !d.isCancelled())
                .map(AccountDetail::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<AccountItemResponse> items = details.stream().map(accountMapper::detailToResponse).toList();
        return accountMapper.toDetailResponse(account, total, items);
    }

    @Override
    @Transactional
    public AccountItemResponse addItem(Long accountId, AddAccountItemRequest request, Long userId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Cuenta", accountId));
        if (!"OPEN".equals(account.status())) {
            throw new BusinessConflictException("La cuenta no está abierta");
        }
        BigDecimal subtotal = request.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        AccountDetail detail = new AccountDetail(null, accountId, request.productId(),
                request.quantity(), request.unitPrice(), subtotal, null, userId, false);
        AccountDetail saved = detailRepository.save(detail);
        return accountMapper.detailToResponse(saved);
    }

    @Override
    @Transactional
    public void cancelItem(Long accountId, Long detailId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Cuenta", accountId));
        if (!"OPEN".equals(account.status())) {
            throw new BusinessConflictException("La cuenta no está abierta");
        }
        AccountDetail detail = detailRepository.findById(detailId).orElseThrow(() -> new ResourceNotFoundException("Detalle", detailId));
        if (detail.isCancelled()) {
            throw new BusinessConflictException("El ítem ya está cancelado");
        }
        AccountDetail cancelled = new AccountDetail(detail.id(), detail.accountId(), detail.productId(),
                detail.quantity(), detail.unitPrice(), detail.subtotal(), detail.addedAt(), detail.addedBy(), true);
        detailRepository.save(cancelled);
    }

    @Override
    @Transactional
    public SaleResponse close(Long accountId, CloseAccountRequest request, Long userId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Cuenta", accountId));
        if (!"OPEN".equals(account.status())) {
            throw new BusinessConflictException("La cuenta no está abierta");
        }
        // Validate cash register is open
        cashRegisterRepository.findById(request.cashRegisterId())
                .filter(cr -> "OPEN".equals(cr.status()))
                .orElseThrow(() -> new BusinessConflictException("La caja no está abierta"));

        // Get non-cancelled details
        List<AccountDetail> details = detailRepository.findByAccountId(accountId).stream()
                .filter(d -> !d.isCancelled()).toList();
        if (details.isEmpty()) {
            throw new BusinessConflictException("La cuenta no tiene ítems activos");
        }

        // Calculate totals
        BigDecimal subtotal = details.stream().map(AccountDetail::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discount = request.discountAmount() != null ? request.discountAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.subtract(discount);

        // Generate sale number and create sale
        String saleNumber = saleRepository.generateSaleNumber(account.branchId());
        Sale sale = new Sale(null, account.branchId(), accountId, request.customerId(),
                request.cashRegisterId(), saleNumber, subtotal, discount, BigDecimal.ZERO, totalAmount,
                request.paymentMethod(), "COMPLETED", Instant.now(), null, null, userId, null, Collections.emptyList());
        Sale savedSale = saleRepository.save(sale);

        // Copy details to sale_details
        List<SaleDetail> saleDetails = details.stream().map(d -> new SaleDetail(null, savedSale.id(),
                d.productId(), d.quantity(), d.unitPrice(), d.subtotal(), BigDecimal.ZERO)).toList();
        saleDetailRepository.saveAll(saleDetails);

        // Create SALE_INCOME movement
        CashRegisterMovement income = new CashRegisterMovement(null, request.cashRegisterId(),
                "SALE_INCOME", totalAmount, "Venta " + saleNumber, null, userId);
        movementRepository.save(income);

        // Close account
        Account closed = new Account(account.id(), account.branchId(), account.customerName(),
                account.customerLastName(), account.tableNumber(), account.internalCode(),
                "CLOSED", account.openedAt(), Instant.now(), account.openedBy(), userId,
                account.notes(), account.createdAt(), account.updatedAt(), Collections.emptyList());
        accountRepository.save(closed);

        // Deduct stock (best-effort)
        List<StockDeductionItem> stockItems = details.stream()
                .map(d -> new StockDeductionItem(d.productId(), d.quantity())).toList();
        inventoryClient.deductStock(stockItems, account.branchId());

        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "SALES",
                "Sale", savedSale.id(), null, null, null, "Venta generada desde cuenta: " + saleNumber));

        return saleMapper.toResponse(savedSale);
    }

    @Override
    @Transactional
    public void cancel(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Cuenta", accountId));
        if (!"OPEN".equals(account.status())) {
            throw new BusinessConflictException("Solo se pueden cancelar cuentas abiertas");
        }
        Account cancelled = new Account(account.id(), account.branchId(), account.customerName(),
                account.customerLastName(), account.tableNumber(), account.internalCode(),
                "CANCELLED", account.openedAt(), Instant.now(), account.openedBy(), null,
                account.notes(), account.createdAt(), account.updatedAt(), Collections.emptyList());
        accountRepository.save(cancelled);
        eventPublisher.publishEvent(new AuditEvent(null, null, "UPDATE", "SALES",
                "Account", accountId, null, null, null, "Cuenta cancelada"));
    }

    private BigDecimal calculateTotal(Long accountId) {
        return detailRepository.findByAccountId(accountId).stream()
                .filter(d -> !d.isCancelled())
                .map(AccountDetail::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
