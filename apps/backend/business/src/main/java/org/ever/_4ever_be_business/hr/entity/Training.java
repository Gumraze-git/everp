package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;

@Entity
@Table(name="training")
@NoArgsConstructor
@Getter
public class Training extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name="training_name")
    private String trainingName;

    @Column(name="training_category")
    @Enumerated(EnumType.STRING)
    private TrainingCategory category;

    @Column(name="duration_hours")
    private Long durationHours;

    @Column(name="delivery_method")
    private String deliveryMethod;

    @Column(name="enrolled")
    private Long enrolled;

    @Column(name="capacity")
    private Integer capacity;

    @Column(name="description", length = 50)
    private String description;

    @Column(name="status")
    private Boolean status;

    @Column(name="training_status")
    @Enumerated(EnumType.STRING)
    private TrainingStatus trainingStatus;

    public Training(String trainingName, TrainingCategory category, Long durationHours, String deliveryMethod, Long enrolled, Integer capacity, String description, Boolean status, TrainingStatus trainingStatus) {
        this.trainingName = trainingName;
        this.category = category;
        this.durationHours = durationHours;
        this.deliveryMethod = deliveryMethod;
        this.enrolled = enrolled;
        this.capacity = capacity;
        this.description = description;
        this.status = status;
        this.trainingStatus = trainingStatus;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }

    /**
     * 교육 프로그램 정보 수정
     *
     * @param trainingName   교육 프로그램명
     * @param trainingStatus 교육 상태
     */
    public void updateTrainingProgram(String trainingName, TrainingStatus trainingStatus) {
        if (trainingName != null && !trainingName.isEmpty()) {
            this.trainingName = trainingName;
        }
        if (trainingStatus != null) {
            this.trainingStatus = trainingStatus;
        }
    }
}
