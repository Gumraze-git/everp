package org.ever._4ever_be_business.order.dao;

import org.ever._4ever_be_business.order.entity.Quotation;
import org.ever._4ever_be_business.sd.dto.request.CreateQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationDetailDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;
import org.ever._4ever_be_business.sd.dto.response.ScmQuotationListItemDto;
import org.ever._4ever_be_business.sd.vo.QuotationSearchConditionVo;
import org.ever._4ever_be_business.sd.vo.ScmQuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface QuotationDAO {
    /**
     * 견적 상세 조회
     *
     * @param quotationId 견적 ID
     * @return 견적 상세 정보
     */
    Optional<QuotationDetailDto> findQuotationDetailById(String quotationId);

    /**
     * 견적 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 견적 목록
     */
    Page<QuotationListItemDto> findQuotationList(QuotationSearchConditionVo condition, Pageable pageable);

    /**
     * SCM용 견적 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return SCM 견적 목록
     */
    Page<ScmQuotationListItemDto> findScmQuotationList(ScmQuotationSearchConditionVo condition, Pageable pageable);

    /**
     * 견적서 생성
     *
     * @param quotation 견적서 엔티티
     * @return 저장된 견적서
     */
    Quotation saveQuotation(Quotation quotation);

    /**
     * 견적서 엔티티 조회
     *
     * @param quotationId 견적서 ID
     * @return 견적서 엔티티
     */
    Optional<Quotation> findQuotationEntityById(String quotationId);
}
