package org.ever._4ever_be_business.fcm.dao;

import org.ever._4ever_be_business.fcm.dto.response.SalesStatementDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface SalesStatementDAO {
    /**
     * 매출전표 상세 정보 조회
     *
     * @param statementId 전표 ID (SalesVoucher ID)
     * @return 매출전표 상세 정보
     */
    Optional<SalesStatementDetailDto> findSalesStatementDetailById(String statementId);

    /**
     * 매출전표 목록 조회 (페이징, 필터링)
     *
     * @param company 거래처명 (optional)
     * @param startDate 시작일 (optional)
     * @param endDate 종료일 (optional)
     * @param pageable 페이징 정보
     * @return 매출전표 목록
     */
    Page<SalesStatementListItemDto> findSalesStatementList(String company, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
