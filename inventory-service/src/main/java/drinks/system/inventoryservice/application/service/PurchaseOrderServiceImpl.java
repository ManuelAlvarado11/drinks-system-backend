package drinks.system.inventoryservice.application.service;

import drinks.system.inventoryservice.application.dto.request.CreatePurchaseOrderRequest;
import drinks.system.inventoryservice.application.dto.request.ReceivePurchaseOrderRequest;
import drinks.system.inventoryservice.application.dto.response.PurchaseOrderDetailResponse;
import drinks.system.inventoryservice.application.dto.response.PurchaseOrderResponse;
import drinks.system.inventoryservice.application.mapper.PurchaseOrderMapper;
import drinks.system.inventoryservice.domain.model.*;
import drinks.system.inventoryservice.domain.port.in.PurchaseOrderUseCase;
import drinks.system.inventoryservice.domain.port.out.*;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderUseCase {
    private final PurchaseOrderRepositoryPort orderRepository;
    private final PurchaseOrderDetailRepositoryPort detailRepository;
    private final ProductStockRepositoryPort stockRepository;
    private final InventoryMovementRepositoryPort movementRepository;
    private final PurchaseOrderMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override @Transactional
    public PurchaseOrderResponse create(CreatePurchaseOrderRequest req, Long userId) {
        String orderNumber = orderRepository.generateOrderNumber();
        BigDecimal total = req.items().stream()
                .map(i -> i.unitCost().multiply(BigDecimal.valueOf(i.quantityOrdered())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PurchaseOrder order = new PurchaseOrder(null, req.supplierId(), req.branchId(),
                orderNumber, "PENDING", total, null, null, null, null,
                userId, null, Collections.emptyList());
        PurchaseOrder saved = orderRepository.save(order);

        List<PurchaseOrderDetail> details = req.items().stream().map(i -> new PurchaseOrderDetail(
                null, saved.id(), i.productId(), i.quantityOrdered(), 0,
                i.unitCost(), i.unitCost().multiply(BigDecimal.valueOf(i.quantityOrdered()))
        )).toList();
        detailRepository.saveAll(details);

        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "INVENTORY",
                "PurchaseOrder", saved.id(), null, null, null, "OC creada: " + orderNumber));
        return mapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<PurchaseOrderResponse> findAll(Pageable pageable, Long supplierId,
            Long branchId, String status, Instant dateFrom, Instant dateTo) {
        Page<PurchaseOrder> page = orderRepository.findAll(pageable, supplierId, branchId, status, dateFrom, dateTo);
        List<PurchaseOrderResponse> content = page.getContent().stream().map(mapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override @Transactional(readOnly = true)
    public PurchaseOrderDetailResponse findById(Long id) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));
        List<PurchaseOrderDetail> details = detailRepository.findByOrderId(id);
        var items = details.stream().map(mapper::detailToResponse).toList();
        return mapper.toDetailResponse(order, items);
    }

    @Override @Transactional
    public void receive(Long id, ReceivePurchaseOrderRequest req, Long userId) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));
        if ("CANCELLED".equals(order.status()) || "RECEIVED".equals(order.status())) {
            throw new BusinessConflictException("La orden no puede recibir mercadería en estado: " + order.status());
        }

        List<PurchaseOrderDetail> existingDetails = detailRepository.findByOrderId(id);
        Map<Long, PurchaseOrderDetail> detailMap = existingDetails.stream()
                .collect(Collectors.toMap(PurchaseOrderDetail::id, d -> d));

        boolean allReceived = true;
        for (ReceivePurchaseOrderRequest.ReceivedItem item : req.items()) {
            PurchaseOrderDetail detail = detailMap.get(item.detailId());
            if (detail == null) continue;
            int newReceived = detail.quantityReceived() + item.quantityReceived();
            PurchaseOrderDetail updated = new PurchaseOrderDetail(detail.id(), detail.purchaseOrderId(),
                    detail.productId(), detail.quantityOrdered(), newReceived, detail.unitCost(), detail.subtotal());
            detailRepository.saveAll(List.of(updated));

            // Update stock
            ProductStock stock = stockRepository.findByProductIdAndBranchId(detail.productId(), order.branchId())
                    .orElse(new ProductStock(null, detail.productId(), order.branchId(), 0, 0, null));
            int prevStock = stock.currentStock();
            int newStock = prevStock + item.quantityReceived();
            stockRepository.save(new ProductStock(stock.id(), stock.productId(), stock.branchId(),
                    newStock, stock.minimumStock(), stock.updatedAt()));
            movementRepository.save(new InventoryMovement(null, detail.productId(), order.branchId(),
                    "PURCHASE", item.quantityReceived(), prevStock, newStock, "PurchaseOrder", id, null, null, userId));

            if (newReceived < detail.quantityOrdered()) allReceived = false;
        }

        String newStatus = allReceived ? "RECEIVED" : "PARTIAL";
        PurchaseOrder updated = new PurchaseOrder(order.id(), order.supplierId(), order.branchId(),
                order.orderNumber(), newStatus, order.totalAmount(), order.orderDate(),
                allReceived ? Instant.now() : order.receivedDate(),
                order.createdAt(), order.updatedAt(), order.createdBy(), userId, Collections.emptyList());
        orderRepository.save(updated);

        eventPublisher.publishEvent(new AuditEvent(userId, null, "UPDATE", "INVENTORY",
                "PurchaseOrder", id, null, null, null, "OC recibida (" + newStatus + "): " + order.orderNumber()));
    }

    @Override @Transactional
    public void cancel(Long id, Long userId) {
        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra", id));
        if (!"PENDING".equals(order.status())) {
            throw new BusinessConflictException("Solo se pueden cancelar órdenes en estado PENDING");
        }
        PurchaseOrder cancelled = new PurchaseOrder(order.id(), order.supplierId(), order.branchId(),
                order.orderNumber(), "CANCELLED", order.totalAmount(), order.orderDate(),
                order.receivedDate(), order.createdAt(), order.updatedAt(), order.createdBy(), userId,
                Collections.emptyList());
        orderRepository.save(cancelled);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "UPDATE", "INVENTORY",
                "PurchaseOrder", id, null, null, null, "OC cancelada: " + order.orderNumber()));
    }
}
