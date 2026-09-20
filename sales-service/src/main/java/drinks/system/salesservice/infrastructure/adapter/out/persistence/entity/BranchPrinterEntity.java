package drinks.system.salesservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "branch_printers", schema = "sales")
@Getter
@Setter
@NoArgsConstructor
public class BranchPrinterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String host;

    @Column(nullable = false)
    private Integer port = 9100;

    @Column(name = "paper_width_mm", nullable = false)
    private Integer paperWidthMm = 80;

    @Column(nullable = false)
    private Integer copies = 1;

    @Column(name = "cut_paper", nullable = false)
    private Boolean cutPaper = true;

    @Column(name = "open_drawer", nullable = false)
    private Boolean openDrawer = false;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @PrePersist
    void prePersist() {
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
