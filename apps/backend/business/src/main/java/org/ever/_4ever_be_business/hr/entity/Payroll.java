package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.enums.PayrollStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payroll")
@NoArgsConstructor
@Getter
public class Payroll extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @Column(name="base_salary")
    private BigDecimal baseSalary;

    @Column(name="overtime_salary")
    private BigDecimal overtimeSalary;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private PayrollStatus status;

    @Column(name="net_salary")
    private BigDecimal netSalary;

    @Column(name="pay_date")
    private LocalDateTime payDate;

    @Column(name="base_date")
    private LocalDateTime baseDate;

    public Payroll(Employee employee, BigDecimal baseSalary, BigDecimal overtimeSalary, PayrollStatus status, BigDecimal netSalary, LocalDateTime payDate, LocalDateTime baseDate) {
        this.employee = employee;
        this.baseSalary = baseSalary;
        this.overtimeSalary = overtimeSalary;
        this.status = status;
        this.netSalary = netSalary;
        this.payDate = payDate;
        this.baseDate = baseDate;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }

    /**
     * 급여 지급 완료 처리
     */
    public void markAsPaid() {
        this.status = PayrollStatus.PAYROLL_PAID;
        this.payDate = LocalDateTime.now();
    }
}
