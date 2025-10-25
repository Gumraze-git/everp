package org.ever._4ever_be_auth.auth.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.config.oauth.TokenProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class RefreshTokenCookieAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final Duration refreshTokenTtl;
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenResponseHttpMessageConverter;

    public RefreshTokenCookieAuthenticationSuccessHandler(TokenProperties tokenProperties) {
        this.refreshTokenTtl = tokenProperties.getRefreshTokenTtl();
        this.accessTokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AccessTokenAuthenticationToken tokenAuthentication)) {
            throw new ServletException("지원되지 않는 인증 타입입니다: " + authentication.getClass());
        }

        OAuth2AccessToken accessToken = tokenAuthentication.getAccessToken();

        if (tokenAuthentication.getRefreshToken() != null) {
            addRefreshTokenCookie(response, tokenAuthentication.getRefreshToken().getTokenValue());
        }

        OAuth2AccessTokenResponse sanitizedResponse = sanitizeResponse(
                accessToken.getTokenValue(),
                accessToken.getTokenType(),
                accessToken.getIssuedAt(),
                accessToken.getExpiresAt(),
                accessToken.getScopes(),
                tokenAuthentication.getAdditionalParameters()
        );

        response.setStatus(HttpServletResponse.SC_OK);
        accessTokenResponseHttpMessageConverter.write(
                sanitizedResponse,
                MediaType.APPLICATION_JSON,
                new ServletServerHttpResponse(response)
        );
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        if (refreshToken == null) {
            return;
        }

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenTtl)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private OAuth2AccessTokenResponse sanitizeResponse(
            String accessTokenValue,
            OAuth2AccessToken.TokenType tokenType,
            java.time.Instant issuedAt,
            java.time.Instant expiresAt,
            Set<String> scopes,
            Map<String, Object> additionalParameters
    ) {
        OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse.withToken(accessTokenValue);

        if (tokenType != null) {
            builder.tokenType(tokenType);
        }

        if (issuedAt != null && expiresAt != null) {
            long expiresIn = ChronoUnit.SECONDS.between(issuedAt, expiresAt);
            builder.expiresIn(expiresIn);
        }

        if (!CollectionUtils.isEmpty(scopes)) {
            builder.scopes(scopes);
        }

        if (!CollectionUtils.isEmpty(additionalParameters)) {
            builder.additionalParameters(additionalParameters);
        }

        return builder.build();
    }
}
