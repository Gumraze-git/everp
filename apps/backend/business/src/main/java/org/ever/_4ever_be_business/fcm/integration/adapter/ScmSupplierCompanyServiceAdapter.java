package org.ever._4ever_be_business.fcm.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompaniesResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.SupplierCompanyResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.SupplierCompanyServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * SCM 서버의 SupplierCompany 서비스와 통신하는 Adapter
 * prod 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "external.mock.enabled", havingValue = "false")
public class ScmSupplierCompanyServiceAdapter implements SupplierCompanyServicePort {

    private final RestClient restClient;

    @Value("${external.scm.url}")
    private String scmServiceUrl;

    @Override
    public SupplierCompanyResponseDto getSupplierCompanyById(String supplierCompanyId) {
        log.info("SCM SupplierCompany 서비스 호출 - supplierCompanyId: {}", supplierCompanyId);

        try {
            Map<String, String> requestBody = Map.of("supplierCompanyId", supplierCompanyId);

            ApiResponse<SupplierCompanyResponseDto> response = restClient.post()
                    .uri(scmServiceUrl + "/scm/scm-pp/company/supplier/single")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<SupplierCompanyResponseDto>>() {});

            if (response != null && response.isSuccess()) {
                log.info("SCM SupplierCompany 서비스 호출 성공 - supplierCompanyId: {}", supplierCompanyId);
                return response.getData();
            } else {
                log.error("SCM SupplierCompany 서비스 응답 실패 - response: {}", response);
                throw new RuntimeException("Failed to retrieve supplier company information from SCM service");
            }
        } catch (Exception e) {
            log.error("SCM SupplierCompany 서비스 호출 중 오류 발생", e);
            throw new RuntimeException("Error calling SCM SupplierCompany service", e);
        }
    }

    @Override
    public SupplierCompaniesResponseDto getSupplierCompaniesByIds(List<String> supplierCompanyIds) {
        log.info("SCM SupplierCompany 서비스 호출 (multiple) - supplierCompanyIds: {}", supplierCompanyIds);

        try {
            Map<String, List<String>> requestBody = Map.of("supplierCompanyIds", supplierCompanyIds);

            ApiResponse<SupplierCompaniesResponseDto> response = restClient.post()
                    .uri(scmServiceUrl + "/scm/scm-pp/company/supplier/multiple")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<SupplierCompaniesResponseDto>>() {});

            if (response != null && response.isSuccess()) {
                log.info("SCM SupplierCompany 서비스 호출 성공 (multiple) - count: {}", supplierCompanyIds.size());
                return response.getData();
            } else {
                log.error("SCM SupplierCompany 서비스 응답 실패 (multiple) - response: {}", response);
                throw new RuntimeException("Failed to retrieve supplier companies from SCM service");
            }
        } catch (Exception e) {
            log.error("SCM SupplierCompany 서비스 호출 중 오류 발생 (multiple)", e);
            throw new RuntimeException("Error calling SCM SupplierCompany service (multiple)", e);
        }
    }

    @Override
    public String getSupplierCompanyIdByUserId(String supplierUserId) {
        log.info("SCM SupplierCompany 서비스 호출 (by userId) - supplierUserId: {}", supplierUserId);

        try {
            Map<String, String> requestBody = Map.of("supplierUserId", supplierUserId);

            ApiResponse<Map<String, String>> response = restClient.post()
                    .uri(scmServiceUrl + "/scm/scm-pp/company/supplier")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Map<String, String>>>() {});

            if (response != null && response.isSuccess() && response.getData() != null) {
                String supplierCompanyId = response.getData().get("supplierCompanyId");
                log.info("SCM SupplierCompany 서비스 호출 성공 - supplierUserId: {}, supplierCompanyId: {}",
                        supplierUserId, supplierCompanyId);
                return supplierCompanyId;
            } else {
                log.error("SCM SupplierCompany 서비스 응답 실패 (by userId) - response: {}", response);
                throw new RuntimeException("Failed to retrieve supplier company ID from SCM service");
            }
        } catch (Exception e) {
            log.error("SCM SupplierCompany 서비스 호출 중 오류 발생 (by userId)", e);
            throw new RuntimeException("Error calling SCM SupplierCompany service (by userId)", e);
        }
    }
}
