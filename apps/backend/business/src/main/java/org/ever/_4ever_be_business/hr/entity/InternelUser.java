package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.audit.Auditable;
import org.ever._4ever_be_business.common.audit.EntityAuditListener;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.enums.Gender;
import org.ever._4ever_be_business.hr.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 내부 직원의 프로필 정보
@Entity
@Table(name="internel_user")
@NoArgsConstructor
@Getter
@EntityListeners(EntityAuditListener.class)
public class InternelUser extends TimeStamp implements Auditable {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name="user_id")
    private String userId;

    @Column(name="name")
    private String name;

    @Column(name="employee_code")
    private String employeeCode;

    @ManyToOne
    @JoinColumn(name="position_id")
    private Position position;

    @Column(name="gender")
    private Gender gender;

    @Column(name="birth_date")
    private LocalDateTime birthDate;

    @Column(name="hire_date")
    private LocalDateTime hireDate;

    @Column(name="address")
    private String address;

    @Column(name="email")
    private String email;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="department_start_at")
    private LocalDateTime departmentStartAt;

    // gateway academicHistory에 해당됨, gateway의 employeeCreateDto 참고
    @Column(name="education")
    private String education;

    // gateway careerHistory 해당됨, gateway의 employeeCreateDto 참고
    @Column(name="career", length = 100)
    private String career;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private UserStatus status;

    public InternelUser(String userId, String name, String employeeCode, Position position, Gender gender, LocalDateTime birthDate, LocalDateTime hireDate, String address, LocalDateTime departmentStartAt, String education, String career) {
        this.userId = userId;
        this.name = name;
        this.employeeCode = employeeCode;
        this.position = position;
        this.gender = gender;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.address = address;
        this.departmentStartAt = departmentStartAt;
        this.education = education;
        this.career = career;
    }

    public InternelUser(
        String id,
        String userId,
        String name,
        String employeeCode,
        Position position,
        Gender gender,
        LocalDateTime birthDate,
        LocalDateTime hireDate,
        String address,
        String email,
        String phoneNumber,
        LocalDateTime departmentStartAt,
        String education,
        String career,
        UserStatus status
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.employeeCode = employeeCode;
        this.position = position;
        this.gender = gender;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.departmentStartAt = departmentStartAt;
        this.education = education;
        this.career = career;
        this.status = status;
    }

    // 내부 사용자 생성
    public static InternelUser createNewInternalUser(
            String userId,
            String name,
            String employeeCode,
            Position position,
            LocalDate birthDate,
            LocalDate hireDate,
            String address,
            String email,
            String phoneNumber,
            UserStatus status
    ) {
        return new InternelUser(
                null,
                userId,
                name,
                employeeCode,
                position,
                null,
                birthDate.atStartOfDay(),
                hireDate.atStartOfDay(),
                address,
                email,
                phoneNumber,
                hireDate.atStartOfDay(),
                null,
                null,
                status
        );
    }

    /**
     * 목업 데이터 생성용 생성자 (ID 포함)
     */
    public InternelUser(String id, String userId, String name, String employeeCode, Position position,
                        Gender gender, LocalDateTime birthDate, LocalDateTime hireDate, String address,
                        String email, String phoneNumber, LocalDateTime departmentStartAt,
                        String education, String career) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.employeeCode = employeeCode;
        this.position = position;
        this.gender = gender;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.departmentStartAt = departmentStartAt;
        this.education = education;
        this.career = career;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }

    /**
     * 직원 정보 수정
     *
     * @param name     이름
     * @param position 직급
     */
    public void updateEmployeeInfo(String name, Position position) {
        if (name != null) {
            this.name = name;
        }
        if (position != null) {
            this.position = position;
        }
        if (this.status == null) {
            this.status = UserStatus.ACTIVE; // 기본값
        }
    }

    /**
     * 프로필 수정 (전화번호, 주소)
     *
     * @param phoneNumber 전화번호
     * @param address 주소
     */
    public void updateProfile(String phoneNumber, String address) {
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if (address != null) {
            this.address = address;
        }
    }

    // Auditable 인터페이스 구현
    @Override
    public String getAuditableId() {
        return id.toString();
    }

    @Override
    public String getAuditableType() {
        return "internelUser";
    }
}
