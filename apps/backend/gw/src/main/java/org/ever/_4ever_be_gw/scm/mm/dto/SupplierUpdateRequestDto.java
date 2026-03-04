package org.ever._4ever_be_gw.scm.mm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierUpdateRequestDto {

    // SupplierCompany 관련 필드들
    private String supplierName;
    private String supplierEmail;
    private String supplierPhone;
    private String supplierBaseAddress;
    private String supplierDetailAddress;
    private String category;
    private String statusCode;
    private Integer deliverLeadTime;

    // SupplierUser 관련 필드들
    private String managerName;
    private String managerPhone;
    private String managerEmail;
}

