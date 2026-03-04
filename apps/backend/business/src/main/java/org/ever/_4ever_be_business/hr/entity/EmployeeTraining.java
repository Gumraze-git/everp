package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus;

@Entity
@Table(name="employee_training")
@NoArgsConstructor
@Getter
public class EmployeeTraining extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name="training_id")
    private Training training;

    @Enumerated(EnumType.STRING)
    @Column(name="completion_status")
    private TrainingCompletionStatus completionStatus;

    public EmployeeTraining(Employee employee, Training training, TrainingCompletionStatus completionStatus) {
        this.employee = employee;
        this.training = training;
        this.completionStatus = completionStatus;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
