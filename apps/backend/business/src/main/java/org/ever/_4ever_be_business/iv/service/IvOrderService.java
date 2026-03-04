package org.ever._4ever_be_business.iv.service;

import org.ever._4ever_be_business.iv.dto.request.IvOrderRequestDto;
import org.ever._4ever_be_business.iv.dto.response.IvOrderResponseDto;

public interface IvOrderService {
    IvOrderResponseDto getOrder(IvOrderRequestDto request);
}
