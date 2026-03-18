package org.ever._4ever_be_gw.user.client;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.restclient.RestClientProvider;
import org.ever._4ever_be_gw.user.dto.CustomerUserProfileResponse;
import org.ever._4ever_be_gw.user.dto.InternalUserProfileResponse;
import org.ever._4ever_be_gw.user.dto.SupplierUserProfileResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * 사용자 유형별 프로필 정보를 원격 서비스에서 조회하는 클라이언트.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileRemoteClient {

    private static final String INTERNAL_PROFILE_PATH = "/hrm/users/internal/{userId}";
    private static final String CUSTOMER_PROFILE_PATH = "/hrm/users/customer/{userId}";
    private static final String SUPPLIER_PROFILE_PATH = "/scm-pp/mm/supplier-users/{userId}";

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final RestClientProvider restClientProvider;

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
        RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);
        try {
            InternalUserProfileResponse response = businessClient.get()
                .uri(INTERNAL_PROFILE_PATH, userId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(InternalUserProfileResponse.class);

            if (response == null) {
                log.warn("[GW][UserProfile] 내부 사용자 이름 조회 실패 - userId: {}, response: {}", userId, response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.userName());
        } catch (RestClientResponseException ex) {
            log.error("[GW][UserProfile] 내부 사용자 이름 조회 중 원격 응답 예외 - userId: {}, status: {}, body: {}",
                userId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("[GW][UserProfile] 내부 사용자 이름 조회 중 예기치 못한 오류 - userId: {}", userId, ex);
            return Optional.empty();
        }
    }

    private Optional<String> fetchCustomerUserName(String userId, String accessToken) {
        RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);
        try {
            CustomerUserProfileResponse response = businessClient.get()
                .uri(CUSTOMER_PROFILE_PATH, userId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(CustomerUserProfileResponse.class);

            if (response == null) {
                log.warn("[GW][UserProfile] 고객 사용자 이름 조회 실패 - userId: {}, response: {}", userId, response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.userName());
        } catch (RestClientResponseException ex) {
            log.error("[GW][UserProfile] 고객 사용자 이름 조회 중 원격 응답 예외 - userId: {}, status: {}, body: {}",
                userId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("[GW][UserProfile] 고객 사용자 이름 조회 중 예기치 못한 오류 - userId: {}", userId, ex);
            return Optional.empty();
        }
    }

    private Optional<String> fetchSupplierUserName(String userId, String accessToken) {
        RestClient scmClient = restClientProvider.getRestClient(ApiClientKey.SCM_PP);
        try {
            SupplierUserProfileResponse response = scmClient.get()
                .uri(SUPPLIER_PROFILE_PATH, userId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(SupplierUserProfileResponse.class);

            if (response == null) {
                log.warn("[GW][UserProfile] 공급사 사용자 이름 조회 실패 - userId: {}, response: {}", userId, response);
                return Optional.empty();
            }

            return Optional.ofNullable(response.userName());
        } catch (RestClientResponseException ex) {
            log.error("[GW][UserProfile] 공급사 사용자 이름 조회 중 원격 응답 예외 - userId: {}, status: {}, body: {}",
                userId, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("[GW][UserProfile] 공급사 사용자 이름 조회 중 예기치 못한 오류 - userId: {}", userId, ex);
            return Optional.empty();
        }
    }
}
