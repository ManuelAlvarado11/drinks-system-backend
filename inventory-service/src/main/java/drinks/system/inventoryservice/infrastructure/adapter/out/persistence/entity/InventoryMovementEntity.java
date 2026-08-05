package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "inventory_movements", schema = "inventory")
@Getter @Setter @NoArgsConstructor
public class InventoryMovementEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @Column(name = "movement_type", nullable = false, length = 30)
    private String movementType;
    @Column(nullable = false)
    private Integer quantity;
    @Column(name = "previous_stock", nullable = false)
    private Integer previousStock;
    @Column(name = "new_stock", nullable = false)
    private Integer newStock;
    @Column(name = "reference_type", length = 50)
    private String referenceType;
    @Column(name = "reference_id")
    private Long referenceId;
    @Column(columnDefinition = "TEXT")
    private String notes;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    @PrePersist void prePersist() { createdAt = Instant.now(); }
}
