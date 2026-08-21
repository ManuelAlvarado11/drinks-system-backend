package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "product_presentations", schema = "inventory")
@Getter @Setter @NoArgsConstructor
public class ProductPresentationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(nullable = false, length = 50)
    private String name;
    @Column(nullable = false)
    private Integer quantity;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @PrePersist void prePersist() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void preUpdate() { updatedAt = Instant.now(); }
}
