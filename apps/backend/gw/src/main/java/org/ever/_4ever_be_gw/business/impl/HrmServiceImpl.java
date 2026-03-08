package org.ever._4ever_be_gw.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.employee.EmployeeCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.business.service.HrmService;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrmServiceImpl implements HrmService {

    private final WebClientProvider webClientProvider;

    @Override
    public Mono<CreateAuthUserResultDto> createInternalUser(EmployeeCreateRequestDto requestDto) {
        log.debug("내부 사용자 등록 요청, payload: {}", requestDto);
        WebClient businessWebClient = webClientProvider.getLongTimeoutWebClient(ApiClientKey.BUSINESS);

        return businessWebClient.post()
                .uri("/hrm/employee-users")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(CreateAuthUserResultDto.class)
                .switchIfEmpty(Mono.error(new IllegalStateException("[ERROR][HRM] HRM 서버 응답 본문이 비었습니다.")))
                .doOnSuccess(response -> log.info("내부 사용자 등록 성공 - data: {}", response))
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error(
                            "[ERROR][HRM] HRM 서버 응답 오류, Status: {}, Body: {}",
                            ex.getStatusCode(),
                            ex.getResponseBodyAsString()
                    );
                })
                .doOnError(err -> log.error("내부 사용자 등록 실패: {}", err.getMessage()));
    }
}
