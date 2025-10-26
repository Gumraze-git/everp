package org.ever._4ever_be_auth.auth.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.entity.RegisteredClientEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisteredClientMapper {

    private static final String DELIMITER = ",";

    private final ObjectMapper objectMapper;

    public RegisteredClientEntity toEntity(RegisteredClient registeredClient) {
        return RegisteredClientEntity.builder()
                .id(registeredClient.getId())
                .clientId(registeredClient.getClientId())
                .clientIdIssuedAt(toLocalDateTime(registeredClient.getClientIdIssuedAt()))
                .clientSecret(registeredClient.getClientSecret())
                .clientSecretExpiresAt(toLocalDateTime(registeredClient.getClientSecretExpiresAt()))
                .clientName(registeredClient.getClientName())
                .clientAuthenticationMethods(join(registeredClient.getClientAuthenticationMethods()
                        .stream()
                        .map(ClientAuthenticationMethod::getValue)
                        .collect(Collectors.toCollection(LinkedHashSet::new))))
                .authorizationGrantTypes(join(registeredClient.getAuthorizationGrantTypes()
                        .stream()
                        .map(AuthorizationGrantType::getValue)
                        .collect(Collectors.toCollection(LinkedHashSet::new))))
                .redirectUris(join(registeredClient.getRedirectUris()))
                .postLogoutRedirectUris(join(registeredClient.getPostLogoutRedirectUris()))
                .scopes(join(registeredClient.getScopes()))
                .clientSettings(writeJson(registeredClient.getClientSettings().getSettings()))
                .tokenSettings(writeJson(registeredClient.getTokenSettings().getSettings()))
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

        builder.clientSettings(readClientSettings(entity.getClientSettings()));
        builder.tokenSettings(readTokenSettings(entity.getTokenSettings()));

        return builder.build();
    }

    private ClientSettings readClientSettings(String json) {
        Map<String, Object> settings = readJson(json);
        ClientSettings.Builder builder = ClientSettings.builder();
        settings.forEach(builder::setting);
        return builder.build();
    }

    private TokenSettings readTokenSettings(String json) {
        Map<String, Object> settings = readJson(json);

        if (settings.isEmpty()) {
            return TokenSettings.builder().build();
        }

        convertDurationSetting(settings, ConfigurationSettingNames.Token.AUTHORIZATION_CODE_TIME_TO_LIVE);
        convertDurationSetting(settings, ConfigurationSettingNames.Token.ACCESS_TOKEN_TIME_TO_LIVE);
        convertDurationSetting(settings, ConfigurationSettingNames.Token.REFRESH_TOKEN_TIME_TO_LIVE);

        return TokenSettings.withSettings(settings).build();
    }

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

    private String writeJson(Map<String, Object> settings) {
        try {
            return objectMapper.writeValueAsString(settings);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize client settings.", e);
            throw new IllegalArgumentException("Failed to serialize client settings.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readJson(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize client settings.", e);
            throw new IllegalArgumentException("Failed to deserialize client settings.", e);
        }
    }

    private LocalDateTime toLocalDateTime(java.time.Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
    }

    private void convertDurationSetting(Map<String, Object> settings, String key) {
        Object value = settings.get(key);
        if (value instanceof String strValue) {
            settings.put(key, Duration.parse(strValue));
        }
    }

}
