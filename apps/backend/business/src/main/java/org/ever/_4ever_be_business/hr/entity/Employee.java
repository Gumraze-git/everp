package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.audit.EntityAuditListener;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;

import java.time.LocalDateTime;

// 내부 직원의 메타 데이터
@Entity
@Table(name="employee")
@NoArgsConstructor
@Getter
@EntityListeners(EntityAuditListener.class)
public class Employee extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="internel_user_id")
    private InternelUser internelUser;

    @Column(name="remaining_vacation")
    private Long remainingVacation;

    @Column(name="last_training_date")
    private LocalDateTime lastTrainingDate;

    public Employee(String id, InternelUser internelUser, Long remainingVacation, LocalDateTime lastTrainingDate) {
        this.id = id;
        this.internelUser = internelUser;
        this.remainingVacation = remainingVacation;
        this.lastTrainingDate = lastTrainingDate;
    }

    public Employee(InternelUser internelUser, Long remainingVacation, LocalDateTime lastTrainingDate) {
        this.internelUser = internelUser;
        this.remainingVacation = remainingVacation;
        this.lastTrainingDate = lastTrainingDate;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
