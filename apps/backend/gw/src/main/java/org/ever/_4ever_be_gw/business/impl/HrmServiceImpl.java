package org.ever._4ever_be_gw.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.employee.EmployeeCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.business.service.HrmService;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.common.dto.RemoteApiResponse;
import org.ever._4ever_be_gw.common.exception.RemoteApiException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
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
    public Mono<RemoteApiResponse<CreateAuthUserResultDto>> createInternalUser(EmployeeCreateRequestDto requestDto) {
        log.debug("내부 사용자 등록 요청, payload: {}", requestDto);

        // 내부 사용자 등록 요청 시의 대기 시간 조정을 위해 longTimeoutWebClient 사용
        WebClient businessWebClient = webClientProvider.getLongTimeoutWebClient(ApiClientKey.BUSINESS);

        return businessWebClient.post() // WebClient를 통해 HTTP POST 요청을 생성함
                .uri("/hrm/employee-users")
                // 요청 본문에 requestDto 객체를 JSON 형태로 직렬화해서 담음.
                .bodyValue(requestDto)
                // retrieve 서버에 요청을 보내고 응답(ClientResponse)를 가져옴.
                .retrieve()
                // 응답 본문을 RemoteApiResponse 형태로 역직렬화 함.
                .bodyToMono(new ParameterizedTypeReference<RemoteApiResponse<CreateAuthUserResultDto>>() {
                })
                .switchIfEmpty(Mono.error(
                        new RemoteApiException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "[ERROR][HRM] HRM 서버 응답 본문이 비었습니다.",
                                null
                        )
                ))
                .flatMap(response -> {
                    if (response == null) {
                        return Mono.error(new RemoteApiException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "[ERROR][HRM] HRM 서버 응답을 파싱할 수 없습니다.",
                                null));
                    }
                    if (!response.isSuccess()) {
                        HttpStatus status = HttpStatus.resolve(response.getStatus());
                        if (status == null) {
                            status = HttpStatus.INTERNAL_SERVER_ERROR;
                        }
                        return Mono.error(new RemoteApiException(status, response.getMessage(), response.getErrors()));
                    }
                    return Mono.just(response);
                })
                .doOnSuccess(response -> log.info("내부 사용자 등록 성공 - status: {}, data: {}", response.getStatus(), response.getData()))
                .doOnError(err -> log.error("내부 사용자 등록 실패: {}", err.getMessage()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error(
                            "[ERROR][HRM] HRM 서버 응답 오류, Status: {}, Body: {}",
                            ex.getStatusCode(),
                            ex.getResponseBodyAsString()
                    );
                    return Mono.error(
                            new RemoteApiException(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    "[ERROR][HRM] HRM 서버에서 내부 사용자 등록 중 오류가 발생했습니다.",
                                    ex.getResponseBodyAsString()
                            )
                    );
                });
    }
}
