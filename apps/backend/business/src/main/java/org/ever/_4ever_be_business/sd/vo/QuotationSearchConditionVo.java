package org.ever._4ever_be_business.sd.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuotationSearchConditionVo {
    private String quotationId;  // 견적 ID로 필터링
    private String customerId;   // 고객사 사용자 ID로 필터링 (CUSTOMER 유저용)
    private String startDate;    // YYYY-MM-DD
    private String endDate;      // YYYY-MM-DD
    private String status;       // PENDING, REVIEW, APPROVAL, REJECTED, ALL
    private String type;         // quotationNumber, customerName, managerName
    private String search;       // 검색어
    private String sort;         // 정렬 (asc, desc)
}
