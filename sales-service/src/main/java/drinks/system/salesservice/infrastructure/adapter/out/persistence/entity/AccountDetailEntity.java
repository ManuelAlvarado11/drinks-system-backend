package drinks.system.salesservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "account_details", schema = "sales")
@Getter
@Setter
@NoArgsConstructor
public class AccountDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    @Column(name = "added_by", nullable = false)
    private Long addedBy;

    @Column(name = "is_cancelled", nullable = false)
    private Boolean isCancelled = false;

    @PrePersist
    void prePersist() {
        if (addedAt == null) addedAt = Instant.now();
    }
}
