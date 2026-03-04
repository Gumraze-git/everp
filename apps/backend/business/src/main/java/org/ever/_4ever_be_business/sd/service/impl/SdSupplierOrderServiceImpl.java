package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.sd.dto.response.SupplierOrderWorkflowItemDto;
import org.ever._4ever_be_business.fcm.integration.port.SupplierCompanyServicePort;
import org.ever._4ever_be_business.sd.service.SdSupplierOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SdSupplierOrderServiceImpl implements SdSupplierOrderService {

    private final SupplierCompanyServicePort supplierCompanyServicePort;

    // TODO: 삭제 필요, 코드 검토 후 삭제 예정
    @Override
    public Page<SupplierOrderWorkflowItemDto> getSupplierOrderList(String supplierUserId, Pageable pageable) {
        log.info("[BUSINESS][SD] 대시보드 공급사 주문서 목록 조회 - userId: {}, page: {}, size: {}",
                supplierUserId, pageable.getPageNumber(), pageable.getPageSize());

        // 2단계: 연동 - 공급사 사용자 → 공급사 회사 ID 조회 (SCM Port)
        String supplierCompanyId = supplierCompanyServicePort.getSupplierCompanyIdByUserId(supplierUserId);
        log.info("[BUSINESS][SD] 공급사 회사 식별자 조회 - userId: {}, supplierCompanyId: {}", supplierUserId, supplierCompanyId);

        // TODO(3단계): supplierCompanyId 기준으로 연관 주문 조회(Repository) 후 SupplierOrderWorkflowItemDto로 매핑
        // 현재 스키마상 Order와 SupplierCompany의 직접 연결이 없어 빈 결과 반환
        return new PageImpl<>(List.of(), pageable, 0);
    }
}
