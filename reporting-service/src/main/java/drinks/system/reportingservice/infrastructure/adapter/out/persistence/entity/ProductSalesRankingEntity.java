package drinks.system.reportingservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "product_sales_ranking", schema = "reporting")
@Getter @Setter @NoArgsConstructor
public class ProductSalesRankingEntity {
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
    @Column(name = "total_quantity_sold", nullable = false)
    private Integer totalQuantitySold = 0;
    @Column(name = "total_revenue", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal profit = BigDecimal.ZERO;
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;
    @Column(name = "refreshed_at", nullable = false)
    private Instant refreshedAt;
}
