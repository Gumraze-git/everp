package org.ever._4ever_be_business.voucher.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.entity.TimeStamp;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="sales_voucher")
@Getter
@NoArgsConstructor
public class SalesVoucher extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "customer_company_id")
    private CustomerCompany customerCompany;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_order_id", length = 36)
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
    private SalesVoucherStatus status;

    @Column(nullable = false, length = 255)
    private String memo;

    public SalesVoucher(CustomerCompany customerCompany, Order order, String voucherCode,
                        LocalDateTime issueDate, LocalDateTime dueDate, BigDecimal totalAmount,
                        SalesVoucherStatus status, String memo) {
        this.customerCompany = customerCompany;
        this.order = order;
        this.voucherCode = voucherCode;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.memo = memo;
    }

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
    public void updateStatus(SalesVoucherStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * 지급 기한을 기준으로 상태를 자동 업데이트합니다.
     * - 현재가 dueDate 이전이고 PAID가 아니면 PENDING
     * - 현재가 dueDate 이후이고 PAID가 아니면 OVERDUE
     */
    public void updateStatusByDueDate() {
        if (this.status == SalesVoucherStatus.PAID) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(this.dueDate)) {
            this.status = SalesVoucherStatus.PENDING;
        } else {
            this.status = SalesVoucherStatus.OVERDUE;
        }
    }

    /**
     * AR 전표 정보를 업데이트합니다.
     *
     * @param status 새로운 상태
     * @param dueDate 새로운 지급 기한
     * @param memo 새로운 메모
     */
    public void updateARInvoice(SalesVoucherStatus status, LocalDateTime dueDate, String memo) {
        if (status != null) {
            this.status = status;
        }
        if (dueDate != null) {
            this.dueDate = dueDate;
        }
        if (memo != null) {
            this.memo = memo;
        }
    }
}
