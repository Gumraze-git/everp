package org.ever._4ever_be_business.voucher.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="purchase_voucher")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PurchaseVoucher extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;
    // scm 연동
    @Column(nullable = false, name = "supplier_company_id", length = 36)
    private String supplierCompanyId;
    // scm 연동
    @Column(nullable = false, name = "product_order_id", length = 36)
    private String productOrderId;

    @Column(nullable = false, name = "voucher_code")
    private String voucherCode;

    @Column(nullable = false, name = "issue_date")
    private LocalDateTime issueDate;

    @Column(nullable = false, name = "due_date")
    private LocalDateTime dueDate;

    @Column(nullable = false, name = "total_amount")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status", length = 20)
    private PurchaseVoucherStatus status;

    @Column(nullable = false, length = 255)
    private String memo;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }

    /**
     * 바우처 상태를 업데이트합니다.
     *
     * @param newStatus 새로운 상태
     */
    public void updateStatus(PurchaseVoucherStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * 지급 기한을 기준으로 상태를 자동 업데이트합니다.
     * - 현재가 dueDate 이전이고 PAID가 아니면 PENDING
     * - 현재가 dueDate 이후이고 PAID가 아니면 OVERDUE
     */
    public void updateStatusByDueDate() {
        if (this.status == PurchaseVoucherStatus.PAID) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(this.dueDate)) {
            this.status = PurchaseVoucherStatus.PENDING;
        } else {
            this.status = PurchaseVoucherStatus.OVERDUE;
        }
    }

}
