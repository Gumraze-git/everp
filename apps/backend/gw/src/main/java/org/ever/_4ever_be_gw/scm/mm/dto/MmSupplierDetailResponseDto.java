package org.ever._4ever_be_gw.scm.mm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MmSupplierDetailResponseDto {
    private String statusCode;
    private SupplierInfo supplierInfo;
    private ManagerInfo managerInfo;

    @Getter
    @Setter
    public static class SupplierInfo {
        private String supplierId;              // 공급사 ID
        private String supplierName;            // 공급사 이름
        private String supplierCode;            // 공급사 코드 (ex. SUP-001)
        private String supplierEmail;           // 공급사 이메일
        private String supplierPhone;           // 공급사 전화번호
        private String supplierBaseAddress;     // 공급사 기본 주소
        private String supplierDetailAddress;   // 공급사 상세 주소
        private String supplierStatus;          // 공급사의 상태(ACTIVE/INACTIVE)
        private String category;                // 자재의 형태
        private Integer deliveryLeadTime;       // 배송 리드 타임
    }

    @Getter
    @Setter
    public static class ManagerInfo {
        private String managerName;     // 담당자 이름
        private String managerPhone;    // 담당자 전화번호
        private String managerEmail;    // 담당자 이메일
    }
}
