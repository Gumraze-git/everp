package org.ever._4ever_be_business.company.dto;

import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.hr.entity.CustomerUser;

/**
 * 고객사 생성 시 생성된 회사와 담당자 정보를 함께 전달하기 위한 DTO.
 */
public record CustomerCreationResult(
    CustomerCompany customerCompany,
    CustomerUser customerUser
) {
}
