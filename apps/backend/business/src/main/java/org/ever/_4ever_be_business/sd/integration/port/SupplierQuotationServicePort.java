package org.ever._4ever_be_business.sd.integration.port;

import java.util.List;

/**
 * 외부(SCM/PP 등)에서 공급사 기준으로 연관된 발주서(Quotation) ID 목록을 조회하기 위한 Port
 */
public interface SupplierQuotationServicePort {

    /**
     * 공급사 회사 ID로 연관된 발주서(Quotation) ID 목록 조회
     * @param supplierCompanyId 공급사 회사 ID
     * @param limit 최대 개수(최근순 우선)
     * @return quotationId 리스트 (최근순)
     */
    List<String> getQuotationIdsBySupplierCompanyId(String supplierCompanyId, int limit);
}

