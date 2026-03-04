package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;

import java.math.BigDecimal;

@Entity
@Table(name="position")
@NoArgsConstructor
@Getter
public class Position extends TimeStamp {

    @Id
    @Column(length = 36, columnDefinition = "VARCHAR(36)")
    private String id;

    private String positionCode;

    private String positionName;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @Column(name="is_manager")
    private Boolean isManager;

    @Column(name="salary")
    private BigDecimal salary;

    public Position(String positionCode, String positionName, Boolean isManager, BigDecimal salary) {
        this.positionCode = positionCode;
        this.positionName = positionName;
        this.isManager = isManager;
        this.salary = salary;
    }

    public Position(String positionCode, String positionName, Department department, Boolean isManager, BigDecimal salary) {
        this.positionCode = positionCode;
        this.positionName = positionName;
        this.department = department;
        this.isManager = isManager;
        this.salary = salary;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
