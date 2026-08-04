package drinks.system.salesservice.application.service;

import drinks.system.salesservice.application.dto.request.CloseCashRegisterRequest;
import drinks.system.salesservice.application.dto.request.CreateMovementRequest;
import drinks.system.salesservice.application.dto.request.OpenCashRegisterRequest;
import drinks.system.salesservice.application.dto.response.CashRegisterDetailResponse;
import drinks.system.salesservice.application.dto.response.CashRegisterMovementResponse;
import drinks.system.salesservice.application.dto.response.CashRegisterResponse;
import drinks.system.salesservice.application.mapper.CashRegisterMapper;
import drinks.system.salesservice.domain.model.CashRegister;
import drinks.system.salesservice.domain.model.CashRegisterMovement;
import drinks.system.salesservice.domain.port.in.CashRegisterUseCase;
import drinks.system.salesservice.domain.port.out.CashRegisterMovementRepositoryPort;
import drinks.system.salesservice.domain.port.out.CashRegisterRepositoryPort;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class CashRegisterServiceImpl implements CashRegisterUseCase {

    private final CashRegisterRepositoryPort cashRegisterRepository;
    private final CashRegisterMovementRepositoryPort movementRepository;
    private final CashRegisterMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public CashRegisterResponse open(OpenCashRegisterRequest request, Long userId, Long branchId) {
        cashRegisterRepository.findOpenByUserIdAndBranchId(userId, branchId).ifPresent(cr -> {
            throw new BusinessConflictException("Ya tiene una caja abierta en esta sucursal");
        });
        CashRegister cr = new CashRegister(null, branchId, userId, request.openingAmount(),
                null, null, null, "OPEN", null, null, null, null, null, userId, userId);
        CashRegister saved = cashRegisterRepository.save(cr);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "SALES",
                "CashRegister", saved.id(), null, null, null, "Caja abierta con monto: " + request.openingAmount()));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CashRegisterResponse> findAll(Pageable pageable, Long branchId, String status, Long userId, Instant dateFrom, Instant dateTo) {
        Page<CashRegister> page = cashRegisterRepository.findAll(pageable, branchId, status, userId, dateFrom, dateTo);
        List<CashRegisterResponse> content = page.getContent().stream().map(mapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public CashRegisterDetailResponse findById(Long id) {
        CashRegister cr = cashRegisterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Caja", id));
        List<CashRegisterMovementResponse> movements = movementRepository.findByCashRegisterId(id).stream()
                .map(mapper::movementToResponse).toList();
        return mapper.toDetailResponse(cr, movements);
    }

    @Override
    @Transactional(readOnly = true)
    public CashRegisterResponse findMyOpen(Long userId) {
        CashRegister cr = cashRegisterRepository.findOpenByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No tiene una caja abierta", userId));
        return mapper.toResponse(cr);
    }

    @Override
    @Transactional
    public CashRegisterResponse close(Long id, CloseCashRegisterRequest request, Long userId) {
        CashRegister cr = cashRegisterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Caja", id));
        if ("CLOSED".equals(cr.status())) {
            throw new BusinessConflictException("La caja ya está cerrada");
        }
        BigDecimal deposits = movementRepository.sumByTypeAndCashRegisterId(id, "DEPOSIT");
        BigDecimal saleIncome = movementRepository.sumByTypeAndCashRegisterId(id, "SALE_INCOME");
        BigDecimal withdrawals = movementRepository.sumByTypeAndCashRegisterId(id, "WITHDRAWAL");
        BigDecimal expectedAmount = cr.openingAmount().add(deposits).add(saleIncome).subtract(withdrawals);
        BigDecimal difference = request.closingAmount().subtract(expectedAmount);

        CashRegister closed = new CashRegister(cr.id(), cr.branchId(), cr.userId(), cr.openingAmount(),
                request.closingAmount(), expectedAmount, difference, "CLOSED",
                cr.openedAt(), Instant.now(), request.notes() != null ? request.notes() : cr.notes(),
                cr.createdAt(), cr.updatedAt(), cr.createdBy(), userId);
        CashRegister saved = cashRegisterRepository.save(closed);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "UPDATE", "SALES",
                "CashRegister", id, null, null, null, "Caja cerrada. Diferencia: " + difference));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CashRegisterMovementResponse addMovement(Long id, CreateMovementRequest request, Long userId) {
        CashRegister cr = cashRegisterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Caja", id));
        if (!"OPEN".equals(cr.status())) {
            throw new BusinessConflictException("La caja no está abierta");
        }
        if ("SALE_INCOME".equals(request.movementType())) {
            throw new BusinessConflictException("Los ingresos por venta se generan automáticamente");
        }
        CashRegisterMovement movement = new CashRegisterMovement(null, id, request.movementType(),
                request.amount(), request.description(), null, userId);
        CashRegisterMovement saved = movementRepository.save(movement);
        return mapper.movementToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CashRegisterMovementResponse> findMovements(Long id) {
        cashRegisterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Caja", id));
        return movementRepository.findByCashRegisterId(id).stream().map(mapper::movementToResponse).toList();
    }
}
