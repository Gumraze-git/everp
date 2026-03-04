package org.ever._4ever_be_business.fcm.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.fcm.integration.dto.ProductMultipleResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.ProductsServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * SCM 서버의 Product 서비스와 통신하는 Adapter
 * prod 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "false")
public class ScmProductsServiceAdapter implements ProductsServicePort {

    private final RestClient restClient;

    @Value("${external.scm.url}")
    private String scmServiceUrl;

    @Override
    public ProductMultipleResponseDto getProductsMultiple(List<String> productIds) {
        log.info("SCM Product 서비스 호출 - productIds: {}", productIds);

        try {
            Map<String, List<String>> requestBody = Map.of("productIds", productIds);

            ApiResponse<ProductMultipleResponseDto> response = restClient.post()
                    .uri(scmServiceUrl + "/scm/scm-pp/product/multiple")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<ProductMultipleResponseDto>>() {});

            if (response != null && response.isSuccess()) {
                log.info("SCM Product 서비스 호출 성공 - productCount: {}",
                        response.getData().getProducts().size());
                return response.getData();
            } else {
                log.error("SCM Product 서비스 응답 실패 - response: {}", response);
                throw new RuntimeException("Failed to retrieve products from SCM service");
            }
        } catch (Exception e) {
            log.error("SCM Product 서비스 호출 중 오류 발생", e);
            throw new RuntimeException("Error calling SCM Product service", e);
        }
    }
}
