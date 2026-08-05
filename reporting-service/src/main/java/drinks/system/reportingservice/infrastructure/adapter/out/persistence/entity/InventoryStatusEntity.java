package drinks.system.reportingservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "inventory_status_view", schema = "reporting")
@Getter @Setter @NoArgsConstructor
public class InventoryStatusEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;
    @Column(name = "category_name", length = 100)
    private String categoryName;
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock = 0;
    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock = 0;
    @Column(name = "cost_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice = BigDecimal.ZERO;
    @Column(name = "sale_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal salePrice = BigDecimal.ZERO;
    @Column(name = "is_low_stock", nullable = false)
    private Boolean isLowStock = false;
    @Column(name = "refreshed_at", nullable = false)
    private Instant refreshedAt;
}
