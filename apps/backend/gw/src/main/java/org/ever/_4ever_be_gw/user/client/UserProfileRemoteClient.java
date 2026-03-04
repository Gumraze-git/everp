package org.ever._4ever_be_gw.user.client;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.dto.RemoteApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.user.dto.CustomerUserProfileResponse;
import org.ever._4ever_be_gw.user.dto.InternalUserProfileResponse;
import org.ever._4ever_be_gw.user.dto.SupplierUserProfileResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * 사용자 유형별 프로필 정보를 원격 서비스에서 조회하는 클라이언트.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileRemoteClient {

    private static final String INTERNAL_PROFILE_PATH = "/hrm/users/internal/{userId}";
    private static final String CUSTOMER_PROFILE_PATH = "/hrm/users/customer/{userId}";
    private static final String SUPPLIER_PROFILE_PATH = "/api/scm-pp/mm/users/supplier/{userId}";

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private static final ParameterizedTypeReference<RemoteApiResponse<InternalUserProfileResponse>> INTERNAL_RESPONSE_TYPE =
        new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<RemoteApiResponse<CustomerUserProfileResponse>> CUSTOMER_RESPONSE_TYPE =
        new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<RemoteApiResponse<SupplierUserProfileResponse>> SUPPLIER_RESPONSE_TYPE =
        new ParameterizedTypeReference<>() {};

    private final WebClientProvider webClientProvider;

    public Optional<String> fetchUserName(String userType, String userId, String accessToken) {
        if (!StringUtils.hasText(userType) || !StringUtils.hasText(userId)) {
            return Optional.empty();
        }

        if (!StringUtils.hasText(accessToken)) {
            log.warn("[GW][UserProfile] 액세스 토큰이 없어 사용자 이름을 조회할 수 없습니다. userId: {}, userType: {}", userId, userType);
            return Optional.empty();
        }

        String normalizedType = userType.toUpperCase(Locale.ROOT);

        return switch (normalizedType) {
            case "INTERNAL" -> fetchInternalUserName(userId, accessToken);
            case "CUSTOMER" -> fetchCustomerUserName(userId, accessToken);
            case "SUPPLIER" -> fetchSupplierUserName(userId, accessToken);
            default -> Optional.empty();
        };
    }

    private Optional<String> fetchInternalUserName(String userId, String accessToken) {
        WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);
        try {
            RemoteApiResponse<InternalUserProfileResponse> response = businessClient.get()
                .uri(INTERNAL_PROFILE_PATH, userId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(INTERNAL_RESPONSE_TYPE)
                .block(REQUEST_TIMEOUT);

            if (response == null || !response.isSuccess() || response.getData() == null) {
                log.warn("[GW][UserProfile] 내부 사용자 이름 조회 실패 - userId: {}, response: {}", userId, response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.getData().userName());
        } catch (WebClientResponseException ex) {
            log.error("[GW][UserProfile] 내부 사용자 이름 조회 중 원격 응답 예외 - userId: {}, status: {}, body: {}",
                userId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("[GW][UserProfile] 내부 사용자 이름 조회 중 예기치 못한 오류 - userId: {}", userId, ex);
            return Optional.empty();
        }
    }

    private Optional<String> fetchCustomerUserName(String userId, String accessToken) {
        WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);
        try {
            RemoteApiResponse<CustomerUserProfileResponse> response = businessClient.get()
                .uri(CUSTOMER_PROFILE_PATH, userId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(CUSTOMER_RESPONSE_TYPE)
                .block(REQUEST_TIMEOUT);

            if (response == null || !response.isSuccess() || response.getData() == null) {
                log.warn("[GW][UserProfile] 고객 사용자 이름 조회 실패 - userId: {}, response: {}", userId, response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.getData().userName());
        } catch (WebClientResponseException ex) {
            log.error("[GW][UserProfile] 고객 사용자 이름 조회 중 원격 응답 예외 - userId: {}, status: {}, body: {}",
                userId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("[GW][UserProfile] 고객 사용자 이름 조회 중 예기치 못한 오류 - userId: {}", userId, ex);
            return Optional.empty();
        }
    }

    private Optional<String> fetchSupplierUserName(String userId, String accessToken) {
        WebClient scmClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);
        try {
            RemoteApiResponse<SupplierUserProfileResponse> response = scmClient.get()
                .uri(SUPPLIER_PROFILE_PATH, userId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(SUPPLIER_RESPONSE_TYPE)
                .block(REQUEST_TIMEOUT);

            if (response == null || !response.isSuccess() || response.getData() == null) {
                log.warn("[GW][UserProfile] 공급사 사용자 이름 조회 실패 - userId: {}, response: {}", userId, response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.getData().userName());
        } catch (WebClientResponseException ex) {
            log.error("[GW][UserProfile] 공급사 사용자 이름 조회 중 원격 응답 예외 - userId: {}, status: {}, body: {}",
                userId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("[GW][UserProfile] 공급사 사용자 이름 조회 중 예기치 못한 오류 - userId: {}", userId, ex);
            return Optional.empty();
        }
    }
}
