package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;

import java.math.BigDecimal;

@Entity
@Table(name="deducation")
@NoArgsConstructor
@Getter
public class Deducation extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name="title")
    private String title;

    @Column(name="amount")
    private BigDecimal amount;

    public Deducation(String title, BigDecimal amount) {
        this.title = title;
        this.amount = amount;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
