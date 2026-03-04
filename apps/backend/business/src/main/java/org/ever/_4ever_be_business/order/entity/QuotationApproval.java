package org.ever._4ever_be_business.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.order.enums.ApprovalStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "quotation_approval")
@NoArgsConstructor
@Getter
public class QuotationApproval extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="approval_status")
    private ApprovalStatus approvalStatus;

    @Column(name="approved_at")
    private LocalDateTime approvedAt;

    @Column(name="approved_by_employee_id", length = 36)
    private String approvedBy;  // Employee ID (UUID)

    @Column(name="rejected_reason")
    private String rejectedReason;

    public QuotationApproval(ApprovalStatus approvalStatus, LocalDateTime approvedAt, String approvedBy, String rejectedReason) {
        this.approvalStatus = approvalStatus;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
        this.rejectedReason = rejectedReason;
    }

    // Constructor for creating new quotation (initially PENDING status)
    public static QuotationApproval createPending() {
        QuotationApproval approval = new QuotationApproval();
        approval.approvalStatus = ApprovalStatus.PENDING;
        approval.approvedAt = null;
        approval.approvedBy = null;
        approval.rejectedReason = null;
        return approval;
    }

    /**
     * 견적서 승인 처리
     */
    public void approve() {
        this.approvalStatus = ApprovalStatus.APPROVAL;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * 견적서 검토 확정 처리
     */
    public void review() {
        this.approvalStatus = ApprovalStatus.REVIEW;
    }

    /**
     * 견적서 승인 및 주문 생성 완료 처리
     * @param employeeId 승인한 직원 ID
     */
    public void approveAndReadyForShipment(String employeeId) {
        this.approvalStatus = ApprovalStatus.APPROVAL;
        this.approvedBy = employeeId;
        this.approvedAt = LocalDateTime.now();
    }

    /**
     * 견적서 거부 처리
     * @param reason 거부 사유
     */
    public void reject(String reason) {
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.rejectedReason = reason;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }

    // Setter methods for saga pattern
    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
        if (approvalStatus == ApprovalStatus.APPROVAL) {
            this.approvedAt = LocalDateTime.now();
        }
    }
}
