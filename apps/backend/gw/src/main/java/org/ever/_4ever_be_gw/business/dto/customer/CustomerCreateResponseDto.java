package org.ever._4ever_be_gw.business.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateResponseDto {
    private String customerId;           // 고객사 ID (UUID 등 String)
    private String customerNumber;       // 고객 번호 (예: C-0001)
    private String customerName;         // 고객사명
    private String ceoName;              // 대표자명
    private String businessNumber;       // 사업자 등록번호 (###-##-#####)

    // 연락처 및 주소
    private String contactPhone;         // 대표 전화번호
    private String contactEmail;         // 대표 이메일
    private String zipCode;              // 우편번호
    private String baseAddress;          // 기본 주소
    private String detailAddress;        // 상세 주소

    // 담당자
    private CustomerManagerDto manager;  // { managerName, managerPhone, managerEmail }

    // 부가 정보
    private String note;                 // 메모/비고
}
