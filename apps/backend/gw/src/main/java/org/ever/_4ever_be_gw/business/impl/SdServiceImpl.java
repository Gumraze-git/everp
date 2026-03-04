package org.ever._4ever_be_gw.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.customer.CustomerCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.business.service.SdService;
import org.ever._4ever_be_gw.common.dto.RemoteApiResponse;
import org.ever._4ever_be_gw.common.exception.RemoteApiException;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SdServiceImpl implements SdService {


    private final WebClientProvider webClientProvider;

    @Override
    public Mono<RemoteApiResponse<CreateAuthUserResultDto>> createCustomer(CustomerCreateRequestDto requestDto) {
        log.debug("고객사 등록 요청 payload: {}", requestDto);

        // 내부 사용자 등록 요청 시의 대기 시간 조정을 위해 longTimeoutWebClient 사용
        WebClient businessClient = webClientProvider.getLongTimeoutWebClient(ApiClientKey.BUSINESS);

        return businessClient.post()
            .uri("/sd/customers")
            .bodyValue(requestDto)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<RemoteApiResponse<CreateAuthUserResultDto>>() {})
                .switchIfEmpty(Mono.error(
                        new RemoteApiException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "[ERROR][SD] SD 서버 응답 본문이 비었습니다.",
                                null
                        )))
            .flatMap(response -> {
                if (response == null) {
                    return Mono.error(new RemoteApiException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "[ERROR][SD] SD 서버 응답을 파싱할 수 없습니다.",
                            null
                    ));
                }

                if (!response.isSuccess()) {
                    HttpStatus status = HttpStatus.resolve(response.getStatus());
                    if (status == null) {
                        status = HttpStatus.INTERNAL_SERVER_ERROR;
                    }
                    return Mono.error(
                            new RemoteApiException(
                                    status,
                                    response.getMessage(),
                                    response.getErrors())
                    );
                }
                return Mono.just(response);
            })

            .doOnSuccess(response ->
                    log.info("[INFO] 고객사 등록 성공 - status: {}, customerId: {}",
                            response.getStatus(),
                            response.getData() != null ? response.getData().getUserId() : null))

            .doOnError(error ->
                    log.error("[ERROR] 고객사 등록 실패: {}",
                            error.getMessage(),
                            error)
            )

            .onErrorResume(
                    WebClientResponseException.class,
                    ex -> {
                log.error("[ERROR] 비즈니스 서버 응답 오류 - status: {}, body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());

                return Mono.error(new RemoteApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "[ERROR] 비즈니스 서버에서 고객사 등록 처리 중 오류가 발생했습니다.",
                    ex.getResponseBodyAsString()
                ));
            });
    }
}
