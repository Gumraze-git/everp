package org.ever._4ever_be_gw.business.service;

import org.ever._4ever_be_gw.business.dto.employee.EmployeeCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.common.dto.RemoteApiResponse;
import reactor.core.publisher.Mono;

/**
 * HRM 서비스 인터페이스
 */
public interface HrmService {

    /**
     * 내부 사용자(직원) 등록
     */
    // 내부 사용자 등록
    // Mono: 비동기 작업 결과가 있거나 없을 수 있음.
    Mono<RemoteApiResponse<CreateAuthUserResultDto>> createInternalUser(EmployeeCreateRequestDto requestDto);
}
