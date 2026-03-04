package org.ever._4ever_be_gw.business.dto.customer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerUpdateRequestDto {
    private String customerName;      // 고객사명
    private String ceoName;           // 대표자명
    private String businessNumber;    // 사업자 번호 (###-##-#####)
    private String customerPhone;     // 고객사 전화번호
    private String customerEmail;     // 고객사 이메일
    private String baseAddress;       // 기본 주소
    private String detailAddress;     // 상세 주소
    private String statusCode;        // 고객사의 활성 비활성화 상태.
    private CustomerManagerDto manager; // 담당자 정보
    private String note;              // 비고
}
