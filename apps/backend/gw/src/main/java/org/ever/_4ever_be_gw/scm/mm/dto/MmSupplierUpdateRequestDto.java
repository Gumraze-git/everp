package org.ever._4ever_be_gw.scm.mm.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class MmSupplierUpdateRequestDto {
    private String statusCode;
    private SupplierInfo supplierInfo;
    private ManagerInfo managerInfo;
    private List<MaterialItems> materialList;

    @Getter
    public static class SupplierInfo {
        private String supplierName;            // 공급업체 이름
        private String supplierEmail;           // 공급업체 이메일
        private String supplierBaseAddress;     // 공급업체 기본 주소
        private String supplierPhone;
        private String supplierDetailAddress;   // 공급업체 상세 주소
        private String category;                // 제공하는 자재의 카테고리
        private Integer deliveryLeadTime;       // 배송 기간
    }

    @Getter
    public static class MaterialItems {     // 제공 자재 목록
        private String materialName;        // 자재 이름
        private String uomCode;             // 자재 단위 코드
        private Integer unitPrice;          // 자재의 단위 가격
    }

    @Getter
    public static class ManagerInfo {
        private String managerName;             // 담당자 이름
        private String managerPhone;            // 담당자 전화번호
        private String managerEmail;            // 담당자 이메일
    }

}

