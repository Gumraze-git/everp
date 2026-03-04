package org.ever._4ever_be_business.hr.service;

import org.ever._4ever_be_business.hr.dto.response.CustomerUserDetailDto;

public interface CustomerUserService {
    /**
     * CustomerUser ID로 고객 사용자 상세 정보 조회
     *
     * @param customerUserId CustomerUser ID
     * @return CustomerUserDetailDto
     */
    CustomerUserDetailDto getCustomerUserDetailByUserId(String customerUserId);
}
