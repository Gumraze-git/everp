package org.ever._4ever_be_gw.business.dto.invoice;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailDto {
    private String invoiceId;            // 전표 ID (UUID)
    private String invoiceNumber;        // 전표 코드
    private String invoiceType;          // 전표 타입(AR/AP)
    private String statusCode;           // 전표 상태 코드 (예: UNPAID, PENDING, PAID)
    private LocalDate issueDate;         // 전표 발행일
    private LocalDate dueDate;           // 전표 납기일

    // 거래처
    private String name;                 // 공급사/고객사 거래처

    // 참조 정보 및 품목
    private String referenceNumber;      // 참조 정보 (구매 주문서)

    // 합계 및 비고
    private Long totalAmount;            // 합계 금액
    private String note;                 // 비고/메모

    // 품목 리스트
    private List<PurchaseInvoiceItemDto> items; // 품목 리스트
}
