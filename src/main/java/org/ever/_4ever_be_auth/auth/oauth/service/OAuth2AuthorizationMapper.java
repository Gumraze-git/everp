package org.ever._4ever_be_auth.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationEntity;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
                .attributes(safeAttributes(authorization.getAttributes()))
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
        builder.accessTokenMetadata(safeMap(token.getMetadata()));
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
        builder.refreshTokenMetadata(safeMap(token.getMetadata()));
    }

    private void setAuthorizationCode(
            OAuth2AuthorizationEntity.OAuth2AuthorizationEntityBuilder builder,
            OAuth2Authorization.Token<OAuth2AuthorizationCode> token) {
        if (token == null) {
            return;
        }
        OAuth2AuthorizationCode authorizationCode = token.getToken();
        builder.authorizationCodeValue(authorizationCode.getTokenValue());
        builder.authorizationCodeIssuedAt(authorizationCode.getIssuedAt());
        builder.authorizationCodeExpiresAt(authorizationCode.getExpiresAt());
        builder.authorizationCodeMetadata(safeMap(token.getMetadata()));
    }

    private OAuth2AccessToken.TokenType resolveAccessTokenType(String tokenType) {
        if (tokenType == null || tokenType.isBlank()) {
            return OAuth2AccessToken.TokenType.BEARER;
        }
        if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(tokenType)) {
            return OAuth2AccessToken.TokenType.BEARER;
        }
        return new OAuth2AccessToken.TokenType(tokenType);
    }

    private Map<String, Object> safeAttributes(Map<String, Object> attributes) {
        return attributes == null || attributes.isEmpty()
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(attributes);
    }

    private Map<String, Object> safeMap(Map<String, Object> map) {
        return map == null ? Collections.emptyMap() : map;
    }
}
