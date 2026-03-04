package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;

import java.time.LocalDateTime;

@Entity
@Table(name="department")
@NoArgsConstructor
@Getter
public class Department extends TimeStamp {

    @Id
    @Column(length = 36, columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name="department_code")
    private String departmentCode;

    @Column(name="department_name")
    private String departmentName;

    @Column(name="description", length = 50)
    private String description;

    @Column(name="establishment_date")
    private LocalDateTime establishmentDate;

    @Column(name="manager_id")
    private String managerId;

    public Department(String departmentCode, String departmentName, String description, LocalDateTime establishmentDate) {
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.description = description;
        this.establishmentDate = establishmentDate;
    }

    /**
     * 부서 정보 수정 (부서장과 설명만 수정 가능)
     *
     * @param managerId   부서장 ID (Employee ID)
     * @param description 설명
     */
    public void updateDepartmentInfo(String managerId, String description) {
        if (managerId != null) {
            this.managerId = managerId;
        }
        if (description != null) {
            this.description = description;
        }
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
