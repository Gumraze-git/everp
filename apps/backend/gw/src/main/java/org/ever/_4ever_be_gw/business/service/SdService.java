package org.ever._4ever_be_gw.business.service;

import org.ever._4ever_be_gw.business.dto.customer.CustomerCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.common.dto.RemoteApiResponse;
import reactor.core.publisher.Mono;

/**
 * SD 서비스 인터페이스
 */
public interface SdService {

    /**
     * 고객사 등록
     */
    Mono<RemoteApiResponse<CreateAuthUserResultDto>> createCustomer(CustomerCreateRequestDto requestDto);
}
