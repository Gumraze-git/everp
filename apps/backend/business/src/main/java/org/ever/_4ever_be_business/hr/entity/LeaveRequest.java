package org.ever._4ever_be_business.hr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.enums.LeaveRequestStatus;
import org.ever._4ever_be_business.hr.enums.LeaveType;

import java.time.LocalDateTime;

@Entity
@Table(name="leave_request")
@NoArgsConstructor
@Getter
public class LeaveRequest extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id")
    private Employee employee;

    @Column(name="leave_type")
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column(name="start_date")
    private LocalDateTime startDate;

    @Column(name="end_date")
    private LocalDateTime endDate;

    @Column(name="number_of_leave_days")
    private Integer numberOfLeaveDays;

    @Column(name="reason")
    private String reason;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private LeaveRequestStatus status;

    public LeaveRequest(Employee employee, LeaveType leaveType, LocalDateTime startDate,
                       LocalDateTime endDate, Integer numberOfLeaveDays, String reason) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfLeaveDays = numberOfLeaveDays;
        this.reason = reason;
        this.status = LeaveRequestStatus.PENDING;  // 신청 시 기본값은 대기 중
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }

    /**
     * 휴가 신청 승인
     */
    public void approve() {
        this.status = LeaveRequestStatus.APPROVED;
    }

    /**
     * 휴가 신청 반려
     */
    public void reject() {
        this.status = LeaveRequestStatus.REJECTED;
    }
}
