package org.ever._4ever_be_business.sd.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoRequestDto;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.ever._4ever_be_business.sd.integration.port.ProductServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * SCM 서버의 Product 서비스와 통신하는 Adapter
 * prod 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "external.mock.enabled", havingValue = "false")
public class ScmProductServiceAdapter implements ProductServicePort {

    private final RestClient restClient;

    @Value("${external.scm.url}")
    private String scmServiceUrl;

    @Override
    public ProductInfoResponseDto getProductsByIds(List<String> productIds) {
        log.info("SCM Product 서비스 호출 - productIds: {}", productIds);

        ProductInfoRequestDto requestDto = new ProductInfoRequestDto(productIds);

        try {
            ApiResponse<ProductInfoResponseDto> response = restClient.post()
                    .uri(scmServiceUrl + "/scm/scm-pp/product/multiple")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestDto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<ProductInfoResponseDto>>() {});

            if (response != null && response.isSuccess()) {
                log.info("SCM Product 서비스 호출 성공 - 조회된 제품 수: {}",
                        response.getData().getProducts().size());
                return response.getData();
            } else {
                log.error("SCM Product 서비스 응답 실패 - response: {}", response);
                throw new RuntimeException("Failed to retrieve product information from SCM service");
            }
        } catch (Exception e) {
            log.error("SCM Product 서비스 호출 중 오류 발생", e);
            throw new RuntimeException("Error calling SCM Product service", e);
        }
    }
}
