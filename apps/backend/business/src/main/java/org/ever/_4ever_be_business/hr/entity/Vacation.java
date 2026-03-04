package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.enums.VacationStatus;
import org.ever._4ever_be_business.hr.enums.VacationType;

@Entity
@Table(name="vacation_request")
@NoArgsConstructor
@Getter
public class Vacation extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @Column(name="vacation_type")
    private VacationType vacationType;

    @Column(name="start_date")
    private String startDate;

    @Column(name="end_date")
    private String endDate;

    @Column(name="status")
    private VacationStatus status;

    public Vacation(Employee employee, VacationType vacationType, String startDate, String endDate, VacationStatus status) {
        this.employee = employee;
        this.vacationType = vacationType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
