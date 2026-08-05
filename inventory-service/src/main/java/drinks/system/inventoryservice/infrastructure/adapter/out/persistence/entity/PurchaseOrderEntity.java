package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "purchase_orders", schema = "inventory")
@Getter @Setter @NoArgsConstructor
public class PurchaseOrderEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @Column(name = "order_number", nullable = false, length = 30)
    private String orderNumber;
    @Column(nullable = false, length = 30)
    private String status = "PENDING";
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Column(name = "order_date", nullable = false)
    private Instant orderDate;
    @Column(name = "received_date")
    private Instant receivedDate;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    @Column(name = "updated_by")
    private Long updatedBy;
    @PrePersist void prePersist() { createdAt = updatedAt = Instant.now(); if (orderDate == null) orderDate = Instant.now(); }
    @PreUpdate void preUpdate() { updatedAt = Instant.now(); }
}
