package org.ever._4ever_be_auth.auth.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.entity.RegisteredClientEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RegisteredClientMapper {

    private static final String DELIMITER = ",";

    private final ObjectMapper plainMapper;

    @Autowired
    public RegisteredClientMapper(@Qualifier("plainJsonObjectMapper") ObjectMapper plainMapper) {
        this.plainMapper = plainMapper;
    }

    @PostConstruct
    void sanityLog() {
        log.info("[RegisteredClientMapper] plainMapper in use = {}", plainMapper);
    }

    public RegisteredClientEntity toEntity(RegisteredClient registeredClient) {
        // CSV 필드 준비
        Set<String> cam = registeredClient.getClientAuthenticationMethods()
                .stream().map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> grants = registeredClient.getAuthorizationGrantTypes()
                .stream().map(AuthorizationGrantType::getValue)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // 3) 공개 클라이언트 + PKCE 일관성 보강
        Map<String, Object> clientSettingsMap = new LinkedHashMap<>(registeredClient.getClientSettings().getSettings());
        normalizePkceClientSettingsIfNeeded(clientSettingsMap, cam, grants);

        Map<String, Object> tokenSettingsMap = registeredClient.getTokenSettings().getSettings();

        return RegisteredClientEntity.builder()
                .id(registeredClient.getId())
                .clientId(registeredClient.getClientId())
                .clientIdIssuedAt(toLocalDateTime(registeredClient.getClientIdIssuedAt()))
                .clientSecret(registeredClient.getClientSecret())
                .clientSecretExpiresAt(toLocalDateTime(registeredClient.getClientSecretExpiresAt()))
                .clientName(registeredClient.getClientName())
                .clientAuthenticationMethods(join(cam))
                .authorizationGrantTypes(join(grants))
                .redirectUris(join(registeredClient.getRedirectUris()))
                .postLogoutRedirectUris(join(registeredClient.getPostLogoutRedirectUris()))
                .scopes(join(registeredClient.getScopes()))
                // 1) writeJson null-safe
                .clientSettings(writeJson(clientSettingsMap))
                .tokenSettings(writeJson(tokenSettingsMap))
                .build();
    }

    public RegisteredClient toRegisteredClient(RegisteredClientEntity entity) {
        if (entity == null) {
            return null;
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getId())
                .clientId(entity.getClientId())
                .clientSecret(entity.getClientSecret())
                .clientName(entity.getClientName());

        LocalDateTime clientIdIssuedAt = entity.getClientIdIssuedAt();
        if (clientIdIssuedAt != null) {
            builder.clientIdIssuedAt(clientIdIssuedAt.atZone(java.time.ZoneId.systemDefault()).toInstant());
        }
        LocalDateTime clientSecretExpiresAt = entity.getClientSecretExpiresAt();
        if (clientSecretExpiresAt != null) {
            builder.clientSecretExpiresAt(clientSecretExpiresAt.atZone(java.time.ZoneId.systemDefault()).toInstant());
        }

        readDelimited(entity.getClientAuthenticationMethods())
                .forEach(value -> builder.clientAuthenticationMethod(new ClientAuthenticationMethod(value)));
        readDelimited(entity.getAuthorizationGrantTypes())
                .forEach(value -> builder.authorizationGrantType(new AuthorizationGrantType(value)));
        readDelimited(entity.getRedirectUris()).forEach(builder::redirectUri);
        readDelimited(entity.getPostLogoutRedirectUris()).forEach(builder::postLogoutRedirectUri);
        readDelimited(entity.getScopes()).forEach(builder::scope);

        // 1) readJson TypeReference 적용
        ClientSettings clientSettings = readClientSettings(entity.getClientSettings());
        TokenSettings tokenSettings = readTokenSettings(entity.getTokenSettings());

        builder.clientSettings(clientSettings);
        builder.tokenSettings(tokenSettings);

        return builder.build();
    }

    /* ===================== JSON 직렬화/역직렬화 보강 ===================== */

    private String writeJson(Map<String, Object> settings) {
        try {
            return plainMapper.writeValueAsString(settings == null ? Collections.emptyMap() : settings);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize client settings.", e);
            throw new IllegalArgumentException("Failed to serialize client settings.", e);
        }
    }

    private Map<String, Object> readJson(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return plainMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize client settings.", e);
            throw new IllegalArgumentException("Failed to deserialize client settings.", e);
        }
    }

    /* ===================== Client/Token Settings 구성 ===================== */

    private ClientSettings readClientSettings(String json) {
        Map<String, Object> settings = readJson(json);
        ClientSettings.Builder builder = ClientSettings.builder();
        settings.forEach(builder::setting);
        return builder.build();
    }

    private TokenSettings readTokenSettings(String json) {
        Map<String, Object> settings = new LinkedHashMap<>(readJson(json));
        if (settings.isEmpty()) {
            return TokenSettings.builder().build();
        }

        // 2) Duration 변환 보강 (String/Number/이미 Duration)
        convertDurationSetting(settings, ConfigurationSettingNames.Token.AUTHORIZATION_CODE_TIME_TO_LIVE);
        convertDurationSetting(settings, ConfigurationSettingNames.Token.ACCESS_TOKEN_TIME_TO_LIVE);
        convertDurationSetting(settings, ConfigurationSettingNames.Token.REFRESH_TOKEN_TIME_TO_LIVE);

        return TokenSettings.withSettings(settings).build();
    }

    /* ===================== 공개 클라이언트(PKCE) 일관성 ===================== */

    /**
     * 공개 클라이언트( client_authentication_methods 에 "none" 포함 )가
     * authorization_code 그랜트를 사용한다면 require_proof_key=true 를 보장.
     * 또한 명시 안 된 경우 require_authorization_consent 기본값을 false로 보정.
     */
    private void normalizePkceClientSettingsIfNeeded(Map<String, Object> clientSettings,
                                                     Set<String> clientAuthMethods,
                                                     Set<String> grantTypes) {
        if (clientSettings == null) return;

        boolean publicClient = clientAuthMethods.stream()
                .anyMatch(v -> "none".equalsIgnoreCase(v));
        boolean usesAuthCode = grantTypes.stream()
                .anyMatch(v -> AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equalsIgnoreCase(v));

        if (publicClient && usesAuthCode) {
            Object requirePk = clientSettings.get(ConfigurationSettingNames.Client.REQUIRE_PROOF_KEY);
            if (!(requirePk instanceof Boolean)) {
                clientSettings.put(ConfigurationSettingNames.Client.REQUIRE_PROOF_KEY, Boolean.TRUE);
            }
        }

        clientSettings.putIfAbsent(ConfigurationSettingNames.Client.REQUIRE_AUTHORIZATION_CONSENT, Boolean.FALSE);
    }

    /* ===================== 유틸 ===================== */

    private String join(Set<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return String.join(DELIMITER, values);
    }

    private Set<String> readDelimited(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(value.split(DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private LocalDateTime toLocalDateTime(java.time.Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
    }

    // 2) 숫자(초)/문자열(ISO-8601)/이미 Duration 모두 수용
    private void convertDurationSetting(Map<String, Object> settings, String key) {
        Object v = settings.get(key);
        if (v == null) return;

        if (v instanceof Duration) return;
        if (v instanceof Number n) {
            settings.put(key, Duration.ofSeconds(n.longValue()));
            return;
        }
        if (v instanceof String s && !s.isBlank()) {
            settings.put(key, Duration.parse(s));
        }
    }
}