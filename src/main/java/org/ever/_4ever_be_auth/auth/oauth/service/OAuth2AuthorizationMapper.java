package org.ever._4ever_be_auth.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationEntity;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
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

    // ===== Domain -> Entity (저장) =====
    public OAuth2AuthorizationEntity toEntity(OAuth2Authorization authorization) {
        Map<String, Object> attrs = new LinkedHashMap<>(authorization.getAttributes());

        Object reqObj = authorization.getAttribute(OAuth2AuthorizationRequest.class.getName());
        if (reqObj instanceof OAuth2AuthorizationRequest req) {
            // 1) 객체로 올라온 경우 → 안전한 Map으로 평탄화
            Map<String, Object> reqMap = new LinkedHashMap<>();
            reqMap.put("authorizationUri", req.getAuthorizationUri());
            reqMap.put("clientId", req.getClientId());
            reqMap.put("redirectUri", req.getRedirectUri());
            reqMap.put("state", req.getState());
            reqMap.put("scopes", new LinkedHashSet<>(req.getScopes()));
            reqMap.put("additionalParameters", new LinkedHashMap<>(req.getAdditionalParameters()));
            attrs.put(OAuth2AuthorizationRequest.class.getName(), reqMap);

        } else if (reqObj instanceof Map<?, ?> m) {
            // 2) 이미 평탄화된 Map으로 들어온 경우 → 키만 String으로 정규화해서 저장
            Map<String, Object> reqMap = toStringObjectMap(m);
            attrs.put(OAuth2AuthorizationRequest.class.getName(), reqMap);
        }
        // else: 없다면 저장 생략

        OAuth2AuthorizationEntity.OAuth2AuthorizationEntityBuilder builder = OAuth2AuthorizationEntity.builder()
                .id(authorization.getId())
                .registeredClientId(authorization.getRegisteredClientId())
                .principalName(authorization.getPrincipalName())
                .authorizationGrantType(authorization.getAuthorizationGrantType().getValue())
                .attributes(sanitizeMap(attrs))
                .state(authorization.getAttribute(STATE));

        // --- Authorization Code
        var code = authorization.getToken(OAuth2AuthorizationCode.class);
        if (code != null) {
            var t = code.getToken();
            builder.authorizationCodeValue(t.getTokenValue());
            builder.authorizationCodeIssuedAt(t.getIssuedAt());
            builder.authorizationCodeExpiresAt(t.getExpiresAt());
            builder.authorizationCodeMetadata(sanitizeMap(code.getMetadata()));
        }

        // --- Access Token
        var access = authorization.getToken(OAuth2AccessToken.class);
        if (access != null) {
            var t = access.getToken();
            builder.accessTokenValue(t.getTokenValue());
            builder.accessTokenIssuedAt(t.getIssuedAt());
            builder.accessTokenExpiresAt(t.getExpiresAt());
            builder.accessTokenMetadata(sanitizeMap(access.getMetadata()));
            if (t.getTokenType() != null) builder.accessTokenType(t.getTokenType().getValue());
            builder.accessTokenScopes(t.getScopes());
        }

        // --- Refresh Token
        var refresh = authorization.getToken(OAuth2RefreshToken.class);
        if (refresh != null) {
            var t = refresh.getToken();
            builder.refreshTokenValue(t.getTokenValue());
            builder.refreshTokenIssuedAt(t.getIssuedAt());
            builder.refreshTokenExpiresAt(t.getExpiresAt());
            builder.refreshTokenMetadata(sanitizeMap(refresh.getMetadata()));
        }

        return builder.build();
    }

    // ===== Entity -> Domain (복원) =====
    public OAuth2Authorization toDomain(OAuth2AuthorizationEntity entity) {
        RegisteredClient rc = registeredClientRepository.findById(entity.getRegisteredClientId());
        if (rc == null) {
            throw new DataRetrievalFailureException(
                    "등록된 클라이언트를 찾을 수 없습니다. registeredClientId=" + entity.getRegisteredClientId());
        }

        Map<String, Object> attrMap = safeMap(entity.getAttributes());
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(rc)
                .id(entity.getId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(new AuthorizationGrantType(entity.getAuthorizationGrantType()))
                .attributes(attrs -> {
                    attrs.putAll(attrMap);
                    if (entity.getState() != null) attrs.put(STATE, entity.getState());
                });

        // --- 평탄화된 OAuth2AuthorizationRequest 복원
        Object reqObj = attrMap.get(OAuth2AuthorizationRequest.class.getName());
        if (reqObj instanceof Map<?, ?> m) {
            String authorizationUri = asString(m.get("authorizationUri"));
            String clientId = asString(m.get("clientId"));
            String redirectUri = asString(m.get("redirectUri"));
            String state = asString(m.get("state"));
            Set<String> scopes = toStringSet(m.get("scopes"));
            Map<String, Object> additional = toStringObjectMap(m.get("additionalParameters"));

            OAuth2AuthorizationRequest restored =
                    OAuth2AuthorizationRequest.authorizationCode()
                            .authorizationUri(authorizationUri)
                            .clientId(clientId)
                            .redirectUri(redirectUri)
                            .scopes(scopes)
                            .state(state)
                            .additionalParameters(additional)
                            .build();

            builder.attributes(attrs ->
                    attrs.put(OAuth2AuthorizationRequest.class.getName(), restored)
            );
        }

        // --- Authorization Code
        if (entity.getAuthorizationCodeValue() != null) {
            OAuth2AuthorizationCode code = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCodeValue(),
                    entity.getAuthorizationCodeIssuedAt(),
                    entity.getAuthorizationCodeExpiresAt()
            );
            builder.token(code, md -> md.putAll(safeMap(entity.getAuthorizationCodeMetadata())));
        }

        // --- Access Token
        if (entity.getAccessTokenValue() != null) {
            OAuth2AccessToken.TokenType tokenType = resolveAccessTokenType(entity.getAccessTokenType());
            Set<String> scopes = Optional.ofNullable(entity.getAccessTokenScopes()).orElseGet(Set::of);

            OAuth2AccessToken at = new OAuth2AccessToken(
                    tokenType,
                    entity.getAccessTokenValue(),
                    entity.getAccessTokenIssuedAt(),
                    entity.getAccessTokenExpiresAt(),
                    scopes
            );
            builder.token(at, md -> md.putAll(safeMap(entity.getAccessTokenMetadata())));
            builder.accessToken(at);
        }

        // --- Refresh Token
        if (entity.getRefreshTokenValue() != null) {
            OAuth2RefreshToken rt = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    entity.getRefreshTokenIssuedAt(),
                    entity.getRefreshTokenExpiresAt()
            );
            builder.token(rt, md -> md.putAll(safeMap(entity.getRefreshTokenMetadata())));
            builder.refreshToken(rt);
        }

        return builder.build();
    }

    private OAuth2AccessToken.TokenType resolveAccessTokenType(String tokenType) {
        if (tokenType == null || tokenType.isBlank()) return OAuth2AccessToken.TokenType.BEARER;
        if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(tokenType)) {
            return OAuth2AccessToken.TokenType.BEARER;
        }
        return new OAuth2AccessToken.TokenType(tokenType);
    }

    private Map<String, Object> safeMap(Map<String, Object> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    private Map<String, Object> sanitizeMap(Map<String, Object> src) {
        if (src == null || src.isEmpty()) return new LinkedHashMap<>();
        Map<String, Object> out = new LinkedHashMap<>(src.size());
        for (Map.Entry<String, Object> e : src.entrySet()) {
            Object v = sanitizeValue(e.getValue());
            if (v != Removed.INSTANCE) out.put(e.getKey(), v);
        }
        return out;
    }

    private Object sanitizeValue(Object v) {
        if (v == null) return null;
        if (v instanceof CharSequence || v instanceof Number || v instanceof Boolean) return v;
        if (v instanceof Enum<?> en) return en.name();
        if (v instanceof TemporalAccessor) return v.toString();
        if (v instanceof Authentication a) return a.getName();
        if (v instanceof UserDetails u) return u.getUsername();

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
        if (v instanceof Collection<?> c) {
            List<Object> list = new ArrayList<>(c.size());
            for (Object o : c) {
                Object sv = sanitizeValue(o);
                if (sv != Removed.INSTANCE) list.add(sv);
            }
            return list.isEmpty() ? Removed.INSTANCE : list;
        }
        if (v.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(v);
            List<Object> list = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                Object sv = sanitizeValue(java.lang.reflect.Array.get(v, i));
                if (sv != Removed.INSTANCE) list.add(sv);
            }
            return list.isEmpty() ? Removed.INSTANCE : list;
        }
        return Removed.INSTANCE;
    }

    private enum Removed { INSTANCE }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toStringObjectMap(Object v) {
        if (!(v instanceof Map<?, ?> m)) return new LinkedHashMap<>();
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<?, ?> e : m.entrySet()) {
            if (e.getKey() instanceof String k) out.put(k, e.getValue());
        }
        return out;
    }

    private String asString(Object v) {
        return (v == null) ? null : String.valueOf(v);
    }

    @SuppressWarnings("unchecked")
    private Set<String> toStringSet(Object v) {
        if (v == null) return new LinkedHashSet<>();
        Collection<?> col;
        if (v instanceof Collection<?> c) {
            col = c;
        } else if (v.getClass().isArray()) {
            int n = java.lang.reflect.Array.getLength(v);
            List<Object> tmp = new ArrayList<>(n);
            for (int i = 0; i < n; i++) tmp.add(java.lang.reflect.Array.get(v, i));
            col = tmp;
        } else {
            col = List.of(v);
        }
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (Object o : col) if (o != null) set.add(String.valueOf(o));
        return set;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toStringObjectMap(Optional<?> v) {
        return v.map(this::toStringObjectMap).orElseGet(LinkedHashMap::new);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toStringObjectMapOrEmpty(Object v) {
        return toStringObjectMap(v);
    }
}