package org.ever._4ever_be_business.order.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.order.dao.QuotationDAO;
import org.ever._4ever_be_business.order.entity.Quotation;
import org.ever._4ever_be_business.order.repository.QuotationRepository;
import org.ever._4ever_be_business.sd.dto.response.QuotationDetailDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;
import org.ever._4ever_be_business.sd.dto.response.ScmQuotationListItemDto;
import org.ever._4ever_be_business.sd.vo.QuotationSearchConditionVo;
import org.ever._4ever_be_business.sd.vo.ScmQuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuotationDAOImpl implements QuotationDAO {

    private final QuotationRepository quotationRepository;

    @Override
    public Optional<QuotationDetailDto> findQuotationDetailById(String quotationId) {
        return quotationRepository.findQuotationDetailById(quotationId);
    }

    @Override
    public Page<QuotationListItemDto> findQuotationList(QuotationSearchConditionVo condition, Pageable pageable) {
        return quotationRepository.findQuotationList(condition, pageable);
    }

    @Override
    public Page<ScmQuotationListItemDto> findScmQuotationList(ScmQuotationSearchConditionVo condition, Pageable pageable) {
        return quotationRepository.findScmQuotationList(condition, pageable);
    }

    @Override
    public Quotation saveQuotation(Quotation quotation) {
        return quotationRepository.save(quotation);
    }

    @Override
    public Optional<Quotation> findQuotationEntityById(String quotationId) {
        return quotationRepository.findById(quotationId);
    }
}
