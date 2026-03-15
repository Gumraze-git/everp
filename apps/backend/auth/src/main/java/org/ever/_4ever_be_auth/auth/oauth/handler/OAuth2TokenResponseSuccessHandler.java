package org.ever._4ever_be_auth.auth.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.config.oauth.TokenProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class OAuth2TokenResponseSuccessHandler implements AuthenticationSuccessHandler {

    // refresh token 쿠키 수명은 설정값을 그대로 사용함.
    private final Duration refreshTokenTtl;
    // SPA 응답 JSON도 전역 Jackson 설정을 따르도록 빈을 주입받음.
    private final ObjectMapper objectMapper;
    // 표준 OAuth2 토큰 응답 본문을 작성할 때 사용하는 converter임.
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenResponseHttpMessageConverter;
    // 쿠키 속성은 로컬/운영 환경에 맞게 외부 설정으로 받음.
    private final String cookieDomain;
    private final String cookieSameSite;
    private final boolean cookieSecure;

    public OAuth2TokenResponseSuccessHandler(
            TokenProperties tokenProperties,
            ObjectMapper objectMapper,
            @Value("${everp.auth.cookie.domain:}") String cookieDomain,
            @Value("${everp.auth.cookie.same-site:Lax}") String cookieSameSite,
            @Value("${everp.auth.cookie.secure:true}") boolean cookieSecure
    ) {
        this.refreshTokenTtl = tokenProperties.getRefreshTokenTtl();
        this.objectMapper = objectMapper;
        this.cookieDomain = cookieDomain;
        this.cookieSameSite = cookieSameSite;
        this.cookieSecure = cookieSecure;
        this.accessTokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        // /oauth2/token 성공 핸들러이므로 기대한 인증 타입인지 먼저 확인함.
        if (!(authentication instanceof OAuth2AccessTokenAuthenticationToken tokenAuthentication)) {
            throw new ServletException("지원되지 않는 인증 타입입니다: " + authentication.getClass());
        }

        // access token은 먼저 쿠키로 내려줌.
        OAuth2AccessToken accessToken = tokenAuthentication.getAccessToken();
        addAccessTokenCookie(response, accessToken);

        // refresh token이 있으면 함께 쿠키로 내려줌.
        if (tokenAuthentication.getRefreshToken() != null) {
            addRefreshTokenCookie(response, tokenAuthentication.getRefreshToken().getTokenValue());
        }

        // 브라우저 SPA는 토큰 본문 대신 최소 상태 정보만 응답함.
        String clientId = tokenAuthentication.getRegisteredClient() != null
                ? tokenAuthentication.getRegisteredClient().getClientId()
                : "";

        if ("everp-spa".equals(clientId)) {
            writeSpaCookieOnlyResponse(response, accessToken);
            return;
        }

        OAuth2AccessTokenResponse tokenResponse = sanitizeResponse(
                accessToken.getTokenValue(),
                accessToken.getTokenType(),
                accessToken.getIssuedAt(),
                accessToken.getExpiresAt(),
                accessToken.getScopes(),
                tokenAuthentication.getAdditionalParameters()
        );

        // 그 외 클라이언트는 표준 OAuth2 토큰 응답을 유지함.
        response.setStatus(HttpServletResponse.SC_OK);
        accessTokenResponseHttpMessageConverter.write(
                tokenResponse,
                MediaType.APPLICATION_JSON,
                new org.springframework.http.server.ServletServerHttpResponse(response)
        );
    }

    private void addAccessTokenCookie(HttpServletResponse response, OAuth2AccessToken accessToken) {
        if (accessToken == null || !StringUtils.hasText(accessToken.getTokenValue())) {
            return;
        }

        // access token 자체의 발급/만료 시각으로 쿠키 TTL을 계산함.
        Instant issuedAt = accessToken.getIssuedAt();
        Instant expiresAt = accessToken.getExpiresAt();
        if (issuedAt == null || expiresAt == null) {
            return;
        }

        Duration accessTokenTtl = Duration.between(issuedAt, expiresAt);
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("access_token", accessToken.getTokenValue(), accessTokenTtl).toString());
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }

        // refresh token은 설정된 TTL을 그대로 사용함.
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("refresh_token", refreshToken, refreshTokenTtl).toString());
    }

    private ResponseCookie buildCookie(String name, String value, Duration maxAge) {
        // access/refresh token 쿠키 속성을 한 곳에서 일관되게 맞춤.
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(maxAge);

        if (StringUtils.hasText(cookieDomain)) {
            builder.domain(cookieDomain);
        }

        return builder.build();
    }

    private void writeSpaCookieOnlyResponse(
            HttpServletResponse response,
            OAuth2AccessToken accessToken
    ) throws IOException {
        // SPA는 쿠키 기반 인증을 쓰므로 body에는 최소 정보만 남김.
        long expiresIn = 0L;
        if (accessToken != null && accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            expiresIn = ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("authenticated", true);
        body.put("token_type", accessToken != null && accessToken.getTokenType() != null
                ? accessToken.getTokenType().getValue()
                : "Bearer");
        body.put("expires_in", expiresIn);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), body);
    }

    private OAuth2AccessTokenResponse sanitizeResponse(
            String accessTokenValue,
            OAuth2AccessToken.TokenType tokenType,
            Instant issuedAt,
            Instant expiresAt,
            Set<String> scopes,
            Map<String, Object> additionalParameters
    ) {
        // 표준 OAuth2 토큰 응답이 필요한 클라이언트를 위한 본문 생성 로직임.
        OAuth2AccessTokenResponse.Builder builder =
                OAuth2AccessTokenResponse.withToken(accessTokenValue);

        if (tokenType != null) {
            builder.tokenType(tokenType);
        }
        if (issuedAt != null && expiresAt != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(issuedAt, expiresAt));
        }
        if (scopes != null && !scopes.isEmpty()) {
            builder.scopes(scopes);
        }
        if (additionalParameters != null && !additionalParameters.isEmpty()) {
            builder.additionalParameters(additionalParameters);
        }

        return builder.build();
    }
}
