package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.response.SalesStatementDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SalesStatementService {
    /**
     * 매출전표 상세 정보 조회
     *
     * @param statementId 전표 ID
     * @return 매출전표 상세 정보
     */
    SalesStatementDetailDto getSalesStatementDetail(String statementId);

    /**
     * 매출전표 목록 조회
     *
     * @param company 거래처명
     * @param startDate 시작일
     * @param endDate 종료일
     * @param pageable 페이징 정보
     * @return 매출전표 목록
     */
    Page<SalesStatementListItemDto> getSalesStatementList(String company, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
