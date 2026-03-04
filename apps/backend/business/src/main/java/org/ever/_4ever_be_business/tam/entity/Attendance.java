package org.ever._4ever_be_business.tam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.tam.enums.AttendanceStatus;

import java.time.LocalDateTime;

@Entity
@Table(name="attendance")
@NoArgsConstructor
@Getter
public class Attendance extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name="work_minutes")
    private Long workMinutes;

    @Column(name="work_date")
    private LocalDateTime workDate;

    @Column(name="status")
    private AttendanceStatus status;

    @Column(name="check_in")
    private LocalDateTime checkIn;

    @Column(name="check_out")
    private LocalDateTime checkOut;

    @Column(name="overtime_minutes")
    private Long overtimeMinutes;

    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;

    public Attendance(Long workMinutes, LocalDateTime workDate, AttendanceStatus status, LocalDateTime checkIn, LocalDateTime checkOut, Long overtimeMinutes, Employee employee) {
        this.workMinutes = workMinutes;
        this.workDate = workDate;
        this.status = status;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.overtimeMinutes = overtimeMinutes;
        this.employee = employee;
    }

    /**
     * 출근 처리
     */
    public void checkIn() {
        this.checkIn = LocalDateTime.now();
        this.status = AttendanceStatus.NORMAL;  // 기본 상태를 NORMAL로 설정
    }

    /**
     * 퇴근 처리
     */
    public void checkOut() {
        this.checkOut = LocalDateTime.now();

        // 근무 시간 계산 (분 단위)
        if (this.checkIn != null) {
            long minutes = java.time.Duration.between(this.checkIn, this.checkOut).toMinutes();
            this.workMinutes = minutes;

            // 기본 근무시간 8시간(480분) 초과 시 초과근무 시간 계산
            if (minutes > 480) {
                this.overtimeMinutes = minutes - 480;
            } else {
                this.overtimeMinutes = 0L;
            }
        }
    }

    /**
     * 새로운 Attendance 생성 (출근용)
     */
    public static Attendance createForCheckIn(Employee employee) {
        Attendance attendance = new Attendance();
        attendance.employee = employee;
        attendance.workDate = LocalDateTime.now();
        attendance.checkIn = LocalDateTime.now();
        attendance.status = AttendanceStatus.NORMAL;
        attendance.workMinutes = 0L;
        attendance.overtimeMinutes = 0L;
        return attendance;
    }

    /**
     * 근태 기록 수정
     */
    public void updateTimeRecord(LocalDateTime checkIn, LocalDateTime checkOut, AttendanceStatus status, Employee employee) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.employee = employee;

        // 근무 시간 계산 (분 단위)
        if (checkIn != null && checkOut != null) {
            long minutes = java.time.Duration.between(checkIn, checkOut).toMinutes();
            this.workMinutes = minutes;

            // 기본 근무시간 8시간(480분) 초과 시 초과근무 시간 계산
            if (minutes > 480) {
                this.overtimeMinutes = minutes - 480;
            } else {
                this.overtimeMinutes = 0L;
            }
        }
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
