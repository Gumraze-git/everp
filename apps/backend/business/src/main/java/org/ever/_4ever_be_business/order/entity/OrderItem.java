package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.order.enums.Unit;

@Entity
@Table(name="order_item")
@NoArgsConstructor
@Getter
public class OrderItem extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @Column(nullable = false, name="product_id", length = 36)
    private String productId;  // UUID

    @Column(nullable = false, name="count")
    private Long count;

    @Column(nullable = false, name="unit")
    private Unit unit;

    @Column(nullable = false, name="price")
    private Long price;

    public OrderItem(Order order, String productId, Long count, Unit unit, Long price) {
        this.order = order;
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
