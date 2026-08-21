package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products", schema = "inventory")
@Getter @Setter @NoArgsConstructor
public class ProductEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(name = "category_id")
    private Long categoryId;
    @Column(length = 50)
    private String size;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "cost_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice = BigDecimal.ZERO;
    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal salePrice = BigDecimal.ZERO;
    @Column(name = "tracks_inventory", nullable = false)
    private Boolean tracksInventory = true;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    @Column(name = "deleted_at")
    private Instant deletedAt;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "updated_by")
    private Long updatedBy;
    @PrePersist void prePersist() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void preUpdate() { updatedAt = Instant.now(); }
}
