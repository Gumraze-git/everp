package org.ever._4ever_be_business.pp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.order.dao.QuotationDAO;
import org.ever._4ever_be_business.pp.dto.request.PpQuotationRequestDto;
import org.ever._4ever_be_business.pp.dto.response.PpQuotationResponseDto;
import org.ever._4ever_be_business.pp.service.PpQuotationService;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;
import org.ever._4ever_be_business.sd.vo.QuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PpQuotationServiceImpl implements PpQuotationService {

    private final QuotationDAO quotationDAO;

    @Override
    public PpQuotationResponseDto getQuotations(PpQuotationRequestDto request) {
        log.info("PP 견적 목록 조회 - quotationId: {}, page: {}, size: {}",
                request.getQuotationId(), request.getPage(), request.getSize());

        // 1. 검색 조건 생성
        QuotationSearchConditionVo condition = new QuotationSearchConditionVo(
                request.getQuotationId(),
                null,  // customerId
                null,  // startDate
                null,  // endDate
                null,  // status
                null,  // type
                null,  // search
                "desc" // sort
        );

        // 2. 페이징 설정
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // 3. 데이터 조회
        Page<QuotationListItemDto> quotationPage = quotationDAO.findQuotationList(condition, pageable);

        // 4. Response DTO 생성
        PpQuotationResponseDto.PageInfo pageInfo = new PpQuotationResponseDto.PageInfo(
                quotationPage.getNumber(),
                quotationPage.getSize(),
                quotationPage.getTotalElements(),
                quotationPage.getTotalPages(),
                quotationPage.hasNext()
        );

        PpQuotationResponseDto response = new PpQuotationResponseDto(
                quotationPage.getContent(),
                pageInfo
        );

        log.info("PP 견적 목록 조회 성공 - totalElements: {}, totalPages: {}",
                quotationPage.getTotalElements(), quotationPage.getTotalPages());

        return response;
    }
}
