package org.ever._4ever_be_business.tam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.enums.VacationType;

import java.time.LocalDateTime;

@Entity
@Table(name="vacation_request")
@NoArgsConstructor
@Getter
public class VacationRequest extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @Column(name="vacation_type")
    private VacationType vacationType;

    @Column(name="requested_start_date")
    private LocalDateTime requestedStartDate;

    @Column(name="requested_end_date")
    private LocalDateTime requestedEndDate;

    public VacationRequest(Employee employee, VacationType vacationType, LocalDateTime requestedStartDate, LocalDateTime requestedEndDate) {
        this.employee = employee;
        this.vacationType = vacationType;
        this.requestedStartDate = requestedStartDate;
        this.requestedEndDate = requestedEndDate;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
