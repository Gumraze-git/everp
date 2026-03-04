package org.ever._4ever_be_business.fcm.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfoResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfosResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.ProductOrderServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * SCM 서버의 ProductOrder 서비스와 통신하는 Adapter
 * prod 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "external.mock.enabled", havingValue = "false")
public class ScmProductOrderServiceAdapter implements ProductOrderServicePort {

    private final RestClient restClient;

    @Value("${external.scm.url}")
    private String scmServiceUrl;

    @Override
    public ProductOrderInfoResponseDto getProductOrderItemsById(String productOrderId) {
        log.info("SCM ProductOrder 아이템 서비스 호출 - productOrderId: {}", productOrderId);

        try {
            Map<String, String> requestBody = Map.of("productOrderId", productOrderId);

            ApiResponse<ProductOrderInfoResponseDto> response = restClient.post()
                    .uri(scmServiceUrl + "/scm/scm-pp/product/orderItem")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<ProductOrderInfoResponseDto>>() {});

            if (response != null && response.isSuccess()) {
                log.info("SCM ProductOrder 아이템 서비스 호출 성공 - productOrderId: {}", productOrderId);
                return response.getData();
            } else {
                log.error("SCM ProductOrder 아이템 서비스 응답 실패 - response: {}", response);
                throw new RuntimeException("Failed to retrieve product order items from SCM service");
            }
        } catch (Exception e) {
            log.error("SCM ProductOrder 아이템 서비스 호출 중 오류 발생", e);
            throw new RuntimeException("Error calling SCM ProductOrder items service", e);
        }
    }

    @Override
    public List<ProductOrderInfosResponseDto.ProductOrderInfoItem> getProductOrderInfosByIds(List<String> productOrderIds) {
        log.info("SCM ProductOrder 정보 서비스 호출 (multiple) - productOrderIds: {}", productOrderIds);

        try {
            Map<String, List<String>> requestBody = Map.of("productOrderIds", productOrderIds);

            ApiResponse<List<ProductOrderInfosResponseDto.ProductOrderInfoItem>> response = restClient.post()
                    .uri(scmServiceUrl + "/scm/scm-pp/product/orderInfos")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<ProductOrderInfosResponseDto.ProductOrderInfoItem>>>() {});

            if (response != null && response.isSuccess()) {
                log.info("SCM ProductOrder 정보 서비스 호출 성공 (multiple) - count: {}", productOrderIds.size());
                return response.getData();
            } else {
                log.error("SCM ProductOrder 정보 서비스 응답 실패 (multiple) - response: {}", response);
                throw new RuntimeException("Failed to retrieve product order infos from SCM service");
            }
        } catch (Exception e) {
            log.error("SCM ProductOrder 정보 서비스 호출 중 오류 발생 (multiple)", e);
            throw new RuntimeException("Error calling SCM ProductOrder infos service (multiple)", e);
        }
    }
}
