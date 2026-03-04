package org.ever._4ever_be_business.pp.service;

import org.ever._4ever_be_business.pp.dto.request.PpQuotationRequestDto;
import org.ever._4ever_be_business.pp.dto.response.PpQuotationResponseDto;

public interface PpQuotationService {
    PpQuotationResponseDto getQuotations(PpQuotationRequestDto request);
}
