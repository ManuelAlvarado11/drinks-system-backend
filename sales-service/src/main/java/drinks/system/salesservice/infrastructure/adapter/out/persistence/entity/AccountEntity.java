package drinks.system.salesservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "accounts", schema = "sales")
@Getter
@Setter
@NoArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Column(name = "customer_last_name", length = 100)
    private String customerLastName;

    @Column(name = "table_number", length = 20)
    private String tableNumber;

    @Column(name = "internal_code", length = 50)
    private String internalCode;

    @Column(nullable = false, length = 30)
    private String status = "OPEN";

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "opened_by", nullable = false)
    private Long openedBy;

    @Column(name = "closed_by")
    private Long closedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = Instant.now();
        if (openedAt == null) openedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
