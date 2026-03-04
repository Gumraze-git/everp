package org.ever._4ever_be_gw.scm.mm.dto;

import lombok.Getter;

@Getter
public class SupplierInfoDto {
    private String businessNumber;          // 사업자 번호
    private String supplierName;            // 공급업체 이름
    private String supplierEmail;           // 공급업체 이메일
    private String supplierPhone;           // 공급업체 전화번호
    private String supplierBaseAddress;     // 공급업체 기본 주소
    private String supplierDetailAddress;   // 공급업체 상세 주소
    private String category;                // 제공하는 자재의 카테고리
    private Integer deliveryLeadTime;       // 배송 기간
}
