package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.order.enums.Unit;

import java.math.BigDecimal;

@Entity
@Table(name="quotation_item")
@NoArgsConstructor
@Getter
public class QuotationItem extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name="quotation_id")
    private Quotation quotation;  // Renamed from quotationId for clarity

    @Column(nullable = false, name="product_id", length = 36)
    private String productId;  // Changed from Long to String for UUID

    @Column(nullable = false, name="count")
    private Long count;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="unit")
    private Unit unit;

    @Column(nullable = false, name="price")
    private BigDecimal price;

    public QuotationItem(Quotation quotation, String productId, Long count, Unit unit, BigDecimal price) {
        this.quotation = quotation;
        this.productId = productId;
        this.count = count;
        this.unit = unit;
        this.price = price;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
