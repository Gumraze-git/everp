package org.ever._4ever_be_gw.scm.mm.service;

import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.common.dto.RemoteApiResponse;
import org.ever._4ever_be_gw.scm.mm.dto.SupplierCreateRequestDto;
import reactor.core.publisher.Mono;

public interface MmService {
    Mono<RemoteApiResponse<CreateAuthUserResultDto>> createSupplier(SupplierCreateRequestDto requestDto);
}
