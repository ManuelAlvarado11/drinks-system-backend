package drinks.system.reportingservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "daily_sales_summary", schema = "reporting")
@Getter @Setter @NoArgsConstructor
public class DailySalesSummaryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "branch_id", nullable = false)
    private Long branchId;
    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;
    @Column(name = "total_sales_count", nullable = false)
    private Integer totalSalesCount = 0;
    @Column(name = "total_revenue", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    @Column(name = "total_discount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    @Column(name = "total_tax", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalTax = BigDecimal.ZERO;
    @Column(name = "net_revenue", nullable = false, precision = 14, scale = 2)
    private BigDecimal netRevenue = BigDecimal.ZERO;
    @Column(name = "refreshed_at", nullable = false)
    private Instant refreshedAt;
}
