package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "product_stock", schema = "inventory", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "branch_id"}))
@Getter @Setter @NoArgsConstructor
public class ProductStockEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;
    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock = 0;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @PrePersist void prePersist() { updatedAt = Instant.now(); }
    @PreUpdate void preUpdate() { updatedAt = Instant.now(); }
}
