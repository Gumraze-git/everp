package org.ever._4ever_be_business.sd.integration.adapter;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.order.entity.Quotation;
import org.ever._4ever_be_business.order.repository.QuotationRepository;
import org.ever._4ever_be_business.sd.integration.port.SupplierQuotationServicePort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 외부 연동이 준비되기 전까지 사용하는 Mock 어댑터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MockSupplierQuotationServiceAdapter implements SupplierQuotationServicePort {

    private final QuotationRepository quotationRepository;

    @Override
    public List<String> getQuotationIdsBySupplierCompanyId(String supplierCompanyId, int limit) {
        log.info("[MOCK] 공급사 발주서 ID 조회 - supplierCompanyId: {}, limit: {}", supplierCompanyId, limit);
        if (limit <= 0) {
            return List.of();
        }

        List<String> quotationIds = quotationRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(0, Math.max(limit * 3, 10)))
                .stream()
                .map(Quotation::getId)
                .filter(StringUtils::hasText)
                .toList();

        if (quotationIds.isEmpty()) {
            return List.of();
        }

        int offset = Math.floorMod(Objects.hashCode(supplierCompanyId), quotationIds.size());
        int resultSize = Math.min(limit, quotationIds.size());

        return IntStream.range(0, resultSize)
                .mapToObj(index -> quotationIds.get((offset + index) % quotationIds.size()))
                .toList();
    }
}
