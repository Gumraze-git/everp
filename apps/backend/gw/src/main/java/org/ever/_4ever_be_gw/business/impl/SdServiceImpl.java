package org.ever._4ever_be_gw.business.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.customer.CustomerCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.business.service.SdService;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SdServiceImpl implements SdService {


    private final WebClientProvider webClientProvider;

    @Override
    public Mono<CreateAuthUserResultDto> createCustomer(CustomerCreateRequestDto requestDto) {
        log.debug("고객사 등록 요청 payload: {}", requestDto);

        // 내부 사용자 등록 요청 시의 대기 시간 조정을 위해 longTimeoutWebClient 사용
        WebClient businessClient = webClientProvider.getLongTimeoutWebClient(ApiClientKey.BUSINESS);

        return businessClient.post()
            .uri("/sd/customers")
            .bodyValue(requestDto)
            .retrieve()
            .bodyToMono(CreateAuthUserResultDto.class)
            .doOnSuccess(response ->
                    log.info("[INFO] 고객사 등록 성공 - customerId: {}",
                            response != null ? response.getUserId() : null))
            .doOnError(error ->
                    log.error("[ERROR] 고객사 등록 실패: {}",
                            error.getMessage(),
                            error)
            );
    }
}
