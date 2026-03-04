package org.ever._4ever_be_business.fcm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대시보드용(공급사) 매입 전표 목록 아이템 DTO
 * - GW의 DashboardWorkflowItemDto와 필드명을 동일하게 맞춰 매핑 비용을 최소화합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPurchaseInvoiceListItemDto {

    /** 전표 식별자 */
    private String itemId;

    /** 전표 번호(예: voucherNo, invoiceCode 등 비즈니스 번호) */
    private String itemNumber;

    /** 한 줄 제목(예: 공급업체명/요약 제목) */
    private String itemTitle;

    /** 담당자/거래 상대 이름(없으면 빈 문자열) */
    private String name;

    /** 상태 코드 */
    private String statusCode;

    /** 일자(ISO-8601 문자열, 예: 2025-11-04) */
    private String date;
}

