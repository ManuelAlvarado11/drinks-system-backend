package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_details", schema = "inventory")
@Getter @Setter @NoArgsConstructor
public class PurchaseOrderDetailEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "purchase_order_id", nullable = false)
    private Long purchaseOrderId;
    @Column(name = "product_id", nullable = false)
    private Long productId;
    @Column(name = "quantity_ordered", nullable = false)
    private Integer quantityOrdered;
    @Column(name = "quantity_received", nullable = false)
    private Integer quantityReceived = 0;
    @Column(name = "unit_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCost;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
