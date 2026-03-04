package org.ever._4ever_be_business.fcm.integration.port;

import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompaniesResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompanyResponseDto;

import java.util.List;

/**
 * SCM 서버의 SupplierCompany 서비스와 통신하기 위한 Port 인터페이스
 */
public interface SupplierCompanyServicePort {
    /**
     * Supplier Company ID로 Supplier Company 정보 조회
     *
     * @param supplierCompanyId Supplier Company ID
     * @return Supplier Company 정보
     */
    SupplierCompanyResponseDto getSupplierCompanyById(String supplierCompanyId);

    /**
     * 여러 Supplier Company ID로 Supplier Company 정보 조회
     *
     * @param supplierCompanyIds Supplier Company ID 목록
     * @return Supplier Company 정보 목록
     */
    SupplierCompaniesResponseDto getSupplierCompaniesByIds(List<String> supplierCompanyIds);

    /**
     * Supplier User ID로 Supplier Company ID 조회
     *
     * @param supplierUserId Supplier User ID
     * @return Supplier Company ID
     */
    String getSupplierCompanyIdByUserId(String supplierUserId);
}
