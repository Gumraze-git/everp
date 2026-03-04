package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.integration.port.SupplierCompanyServicePort;
import org.ever._4ever_be_business.sd.dto.request.SupplierQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.response.ScmQuotationListItemDto;
import org.ever._4ever_be_business.sd.dto.response.SupplierQuotationWorkflowItemDto;
import org.ever._4ever_be_business.sd.service.DashboardSupplierQuotationService;
import org.ever._4ever_be_business.sd.service.QuotationService;
import org.ever._4ever_be_business.sd.vo.ScmQuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.ever._4ever_be_business.sd.integration.port.SupplierQuotationServicePort;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardSupplierQuotationServiceImpl implements DashboardSupplierQuotationService {

    private final QuotationService quotationService;
    private final SupplierCompanyServicePort supplierCompanyServicePort;
    private final SupplierQuotationServicePort supplierQuotationServicePort;

    @Override
    public Page<SupplierQuotationWorkflowItemDto> getSupplierQuotationList(SupplierQuotationRequestDto request, Pageable pageable) {
        String userId = request.getUserId();
        log.info("[BUSINESS][SD] 대시보드 공급사 발주서 목록 조회 - userId: {}, page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        // 1) supplierCompanyId 조회
        String supplierCompanyId = supplierCompanyServicePort.getSupplierCompanyIdByUserId(userId);
        // 2) 외부 연동으로 해당 공급사 관련 quotationId 목록 확보(최근순, limit=size)
        List<String> quotationIds = supplierQuotationServicePort.getQuotationIdsBySupplierCompanyId(
                supplierCompanyId, pageable.getPageSize());

        if (quotationIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 3) 기존 조회를 활용해 목록을 가져오고(최신순), ID로 1차 필터
        ScmQuotationSearchConditionVo condition = new ScmQuotationSearchConditionVo(null, null, null, null);
        Page<ScmQuotationListItemDto> page = quotationService.getScmQuotationList(condition, pageable);

        Set<String> idSet = quotationIds.stream().collect(Collectors.toSet());
        List<SupplierQuotationWorkflowItemDto> filtered = page.getContent().stream()
                .filter(q -> idSet.contains(q.getQuotationId()))
                .map(this::toDashboardItem)
                .collect(Collectors.toList());

        // 4) 최근순 보장을 위해 기존 정렬 유지(상위 N개), 전체 개수는 filtered.size 기준으로 응답
        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    private SupplierQuotationWorkflowItemDto toDashboardItem(ScmQuotationListItemDto src) {
        return SupplierQuotationWorkflowItemDto.builder()
                .itemId(src.getQuotationId())
                .itemNumber(src.getQuotationNumber())
                .itemTitle(src.getCustomerName())
                .name("")
                .statusCode(src.getStatusCode())
                .date(src.getRequestDate())
                .build();
    }
}
