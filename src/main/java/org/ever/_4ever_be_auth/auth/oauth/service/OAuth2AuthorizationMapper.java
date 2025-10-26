package org.ever._4ever_be_auth.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationEntity;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.STATE;

@Component
@RequiredArgsConstructor
public class OAuth2AuthorizationMapper {

    private final RegisteredClientRepository registeredClientRepository;

    public OAuth2AuthorizationEntity toEntity(OAuth2Authorization authorization) {
        OAuth2AuthorizationEntity.OAuth2AuthorizationEntityBuilder builder = OAuth2AuthorizationEntity.builder()
                .id(authorization.getId())
                .registeredClientId(authorization.getRegisteredClientId())
                .principalName(authorization.getPrincipalName())
                .authorizationGrantType(authorization.getAuthorizationGrantType().getValue())
                // ★ attributes 정화 후 저장
                .attributes(sanitizeMap(authorization.getAttributes()))
                .state(authorization.getAttribute(STATE));

        var authorizationCode = authorization.getToken(OAuth2AuthorizationCode.class);
        setAuthorizationCode(builder, authorizationCode);

        var accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            populateAccessToken(builder, accessToken);
        }

        var refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (refreshToken != null) {
            populateRefreshToken(builder, refreshToken);
        }

        return builder.build();
    }

    public OAuth2Authorization toDomain(OAuth2AuthorizationEntity entity) {
        RegisteredClient registeredClient = registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "등록된 클라이언트를 찾을 수 없습니다. registeredClientId=" + entity.getRegisteredClientId());
        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(entity.getAuthorizationGrantType()))
                // ★ 저장 시 정화된 attributes 그대로 주입
                .attributes(attrs -> populateAttributes(attrs, entity));

        if (entity.getAuthorizationCodeValue() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCodeValue(),
                    entity.getAuthorizationCodeIssuedAt(),
                    entity.getAuthorizationCodeExpiresAt());
            builder.token(authorizationCode,
                    metadata -> metadata.putAll(safeMap(entity.getAuthorizationCodeMetadata())));
        }

        if (entity.getAccessTokenValue() != null) {
            OAuth2AccessToken.TokenType tokenType = resolveAccessTokenType(entity.getAccessTokenType());

            Set<String> scopes = Optional
                    .ofNullable(entity.getAccessTokenScopes())
                    .orElse(Collections.emptySet());

            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    tokenType,
                    entity.getAccessTokenValue(),
                    entity.getAccessTokenIssuedAt(),
                    entity.getAccessTokenExpiresAt(),
                    scopes);
            builder.token(accessToken, metadata -> metadata.putAll(safeMap(entity.getAccessTokenMetadata())));
            builder.accessToken(accessToken);
        }

        if (entity.getRefreshTokenValue() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    entity.getRefreshTokenIssuedAt(),
                    entity.getRefreshTokenExpiresAt());
            builder.token(refreshToken, metadata -> metadata.putAll(safeMap(entity.getRefreshTokenMetadata())));
            builder.refreshToken(refreshToken);
        }

        return builder.build();
    }

    private void populateAttributes(Map<String, Object> attributes, OAuth2AuthorizationEntity entity) {
        attributes.putAll(safeMap(entity.getAttributes()));
        if (entity.getState() != null) {
            attributes.put(STATE, entity.getState());
        }
    }

    private void populateAccessToken(
            OAuth2AuthorizationEntity.OAuth2AuthorizationEntityBuilder builder,
            OAuth2Authorization.Token<OAuth2AccessToken> token) {
        OAuth2AccessToken accessToken = token.getToken();
        builder.accessTokenValue(accessToken.getTokenValue());
        builder.accessTokenIssuedAt(accessToken.getIssuedAt());
        builder.accessTokenExpiresAt(accessToken.getExpiresAt());
        // ★ 메타데이터 정화 후 저장
        builder.accessTokenMetadata(sanitizeMap(token.getMetadata()));
        if (accessToken.getTokenType() != null) {
            builder.accessTokenType(accessToken.getTokenType().getValue());
        }
        builder.accessTokenScopes(accessToken.getScopes());
    }

    private void populateRefreshToken(
            OAuth2AuthorizationEntity.OAuth2AuthorizationEntityBuilder builder,
            OAuth2Authorization.Token<OAuth2RefreshToken> token) {
        OAuth2RefreshToken refreshToken = token.getToken();
        builder.refreshTokenValue(refreshToken.getTokenValue());
        builder.refreshTokenIssuedAt(refreshToken.getIssuedAt());
        builder.refreshTokenExpiresAt(refreshToken.getExpiresAt());
        // ★ 메타데이터 정화 후 저장
        builder.refreshTokenMetadata(sanitizeMap(token.getMetadata()));
    }

    private void setAuthorizationCode(
            OAuth2AuthorizationEntity.OAuth2AuthorizationEntityBuilder builder,
            OAuth2Authorization.Token<OAuth2AuthorizationCode> token) {
        if (token == null) return;
        OAuth2AuthorizationCode authorizationCode = token.getToken();
        builder.authorizationCodeValue(authorizationCode.getTokenValue());
        builder.authorizationCodeIssuedAt(authorizationCode.getIssuedAt());
        builder.authorizationCodeExpiresAt(authorizationCode.getExpiresAt());
        // ★ 메타데이터 정화 후 저장
        builder.authorizationCodeMetadata(sanitizeMap(token.getMetadata()));
    }

    private OAuth2AccessToken.TokenType resolveAccessTokenType(String tokenType) {
        if (tokenType == null || tokenType.isBlank()) return OAuth2AccessToken.TokenType.BEARER;
        if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(tokenType)) {
            return OAuth2AccessToken.TokenType.BEARER;
        }
        return new OAuth2AccessToken.TokenType(tokenType);
    }

    // ---------- 정화 유틸 ----------

    private Map<String, Object> safeMap(Map<String, Object> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    /**
     * Map을 안전 값만 남기도록 재귀 정화.
     */
    private Map<String, Object> sanitizeMap(Map<String, Object> src) {
        if (src == null || src.isEmpty()) return Collections.emptyMap();
        Map<String, Object> out = new LinkedHashMap<>(src.size());
        for (Map.Entry<String, Object> e : src.entrySet()) {
            Object v = sanitizeValue(e.getValue());
            if (v != Removed.INSTANCE) {
                out.put(e.getKey(), v);
            }
        }
        return Collections.unmodifiableMap(out);
    }

    /**
     * 값 하나를 안전 타입으로 정화. 제거 대상은 Removed.INSTANCE 반환.
     */
    private Object sanitizeValue(Object v) {
        if (v == null) return null;

        // 허용: 문자열/숫자/불리언
        if (v instanceof CharSequence || v instanceof Number || v instanceof Boolean) return v;

        // 허용: 열거형 → name()
        if (v instanceof Enum<?> en) return en.name();

        // 허용: java.time 계열 → toString(ISO-8601)
        if (v instanceof TemporalAccessor) return v.toString();

        // Authentication → principalName 문자열
        if (v instanceof Authentication a) return a.getName();

        // UserDetails → username 문자열
        if (v instanceof UserDetails u) return u.getUsername();

        // Map → 재귀
        if (v instanceof Map<?, ?> m) {
            Map<String, Object> cast = m.entrySet().stream()
                    .filter(en -> en.getKey() instanceof String)
                    .collect(Collectors.toMap(
                            en -> (String) en.getKey(),
                            Map.Entry::getValue,
                            (a, b) -> b,
                            LinkedHashMap::new
                    ));
            Map<String, Object> sanitized = sanitizeMap(cast);
            return sanitized.isEmpty() ? Removed.INSTANCE : sanitized;
        }

        // Collection/배열 → 재귀
        if (v instanceof Collection<?> c) {
            List<Object> list = new ArrayList<>(c.size());
            for (Object o : c) {
                Object sv = sanitizeValue(o);
                if (sv != Removed.INSTANCE) list.add(sv);
            }
            return list.isEmpty() ? Removed.INSTANCE : List.copyOf(list);
        }
        if (v.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(v);
            List<Object> list = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                Object sv = sanitizeValue(java.lang.reflect.Array.get(v, i));
                if (sv != Removed.INSTANCE) list.add(sv);
            }
            return list.isEmpty() ? Removed.INSTANCE : List.copyOf(list);
        }

        // 그 외 커스텀/복잡 객체는 저장 금지 (보안상 제거)
        return Removed.INSTANCE;
    }

    /**
     * 제거 표시용 센티널
     */
    private enum Removed { INSTANCE }
}