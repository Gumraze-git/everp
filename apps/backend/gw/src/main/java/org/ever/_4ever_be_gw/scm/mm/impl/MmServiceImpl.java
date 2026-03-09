package org.ever._4ever_be_gw.scm.mm.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.scm.mm.dto.SupplierCreateRequestDto;
import org.ever._4ever_be_gw.scm.mm.service.MmService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MmServiceImpl implements MmService {

    private final WebClientProvider webClientProvider;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<CreateAuthUserResultDto> createSupplier(SupplierCreateRequestDto requestDto) {

        WebClient scmPpClient = webClientProvider.getLongTimeoutWebClient(ApiClientKey.SCM_PP);

        return scmPpClient.post()
            .uri("/scm-pp/mm/supplier")
            .bodyValue(requestDto)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .switchIfEmpty(Mono.error(
                new BusinessException(
                    ErrorCode.VENDOR_CREATE_PROCESSING_ERROR,
                    "[MM] 공급사 등록 응답이 비어 있습니다."
                )
            ))
            .map(this::extractCreateSupplierResult)
            .doOnSuccess(response ->
                log.info("[INFO] 공급사 등록 성공 - userId: {}",
                    response != null ? response.getUserId() : null))
            .doOnError(WebClientResponseException.class, ex ->
                log.error("[ERROR] SCM_PP 서버 응답 오류 - status: {}, body: {}",
                    ex.getStatusCode(), ex.getResponseBodyAsString()))
            .doOnError(error ->
                log.error("[ERROR] 공급사 등록 실패: {}", error.getMessage(), error));
    }

    private CreateAuthUserResultDto extractCreateSupplierResult(JsonNode responseBody) {
        if (responseBody == null || responseBody.isNull()) {
            throw new BusinessException(
                ErrorCode.VENDOR_CREATE_PROCESSING_ERROR,
                "[MM] 공급사 등록 응답을 읽을 수 없습니다."
            );
        }

        JsonNode successNode = responseBody.get("success");
        if (successNode != null && successNode.isBoolean() && !successNode.asBoolean()) {
            throw new BusinessException(
                ErrorCode.VENDOR_CREATE_PROCESSING_ERROR,
                responseBody.path("message").asText("공급사 등록 처리에 실패했습니다.")
            );
        }

        JsonNode dataNode = responseBody.get("data");
        if (dataNode == null || dataNode.isNull()) {
            throw new BusinessException(
                ErrorCode.VENDOR_CREATE_PROCESSING_ERROR,
                "[MM] 공급사 등록 응답 data가 비어 있습니다."
            );
        }

        try {
            return objectMapper.treeToValue(dataNode, CreateAuthUserResultDto.class);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(
                ErrorCode.EXTERNAL_API_ERROR,
                "[MM] 공급사 등록 응답 파싱에 실패했습니다.",
                ex
            );
        }
    }
}
