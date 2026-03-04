package org.ever._4ever_be_business.sd.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.sd.dto.request.InventoryCheckRequestDto;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResponseDto;
import org.ever._4ever_be_business.sd.integration.port.InventoryServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * SCM 서버의 Inventory 서비스와 통신하는 Adapter
 * prod 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "external.mock.enabled", havingValue = "false")
public class ScmInventoryServiceAdapter implements InventoryServicePort {

    private final RestClient restClient;

    @Value("${external.scm.url}")
    private String scmServiceUrl;

    @Override
    public InventoryCheckResponseDto checkInventory(InventoryCheckRequestDto requestDto) {
        log.info("SCM Inventory 서비스 호출 - items count: {}", requestDto.getItems().size());

        try {
            ApiResponse<InventoryCheckResponseDto> response = restClient.post()
                    .uri(scmServiceUrl + "/scm/scm-pp/inventory/stock-check")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestDto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<InventoryCheckResponseDto>>() {});

            if (response != null && response.isSuccess()) {
                log.info("SCM Inventory 서비스 호출 성공 - 결과 items count: {}",
                        response.getData().getItems().size());
                return response.getData();
            } else {
                log.error("SCM Inventory 서비스 응답 실패 - response: {}", response);
                throw new RuntimeException("Failed to check inventory from SCM service");
            }
        } catch (Exception e) {
            log.error("SCM Inventory 서비스 호출 중 오류 발생", e);
            throw new RuntimeException("Error calling SCM Inventory service", e);
        }
    }
}
