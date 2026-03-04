package org.ever._4ever_be_business.sd.integration.adapter;

import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.sd.integration.port.SupplierQuotationServicePort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 외부 연동이 준비되기 전까지 사용하는 Mock 어댑터
 */
@Slf4j
@Component
public class MockSupplierQuotationServiceAdapter implements SupplierQuotationServicePort {
    @Override
    public List<String> getQuotationIdsBySupplierCompanyId(String supplierCompanyId, int limit) {
        log.info("[MOCK] 공급사 발주서 ID 조회 - supplierCompanyId: {}, limit: {}", supplierCompanyId, limit);
        return List.of();
    }
}

