package org.ever._4ever_be_auth.auth.account.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.api.auth.LogoutApi;
import org.ever._4ever_be_auth.auth.oauth.service.LogoutService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LogoutController implements LogoutApi {

    private static final String ACCESS_COOKIE_NAME = "access_token";
    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final Map<String, Object> LOGOUT_RESPONSE = Map.of("success", true);

    private final LogoutService logoutService;

    // token cookie 속성은 auth success/failure handler와 동일하게 맞춤.
    @Value("${everp.auth.cookie.domain:}")
    private String authCookieDomain;

    @Value("${everp.auth.cookie.same-site:Lax}")
    private String authCookieSameSite;

    @Value("${everp.auth.cookie.secure:true}")
    private boolean authCookieSecure;

    // 세션 쿠키는 servlet 설정값을 그대로 따름.
    @Value("${server.servlet.session.cookie.name:JSESSIONID}")
    private String sessionCookieName;

    @Value("${server.servlet.session.cookie.path:/}")
    private String sessionCookiePath;

    // Domain은 보통 명시적으로 설정했을 때만 필요합니다. (없으면 생략)
    @Value("${server.servlet.session.cookie.domain:}")
    private String sessionCookieDomain;

    // Spring Boot 3: none / lax / strict 중 하나. 미설정 시 생략
    @Value("${server.servlet.session.cookie.same-site:}")
    private String sessionCookieSameSite;

    @Value("${server.servlet.session.cookie.secure:true}")
    private boolean sessionCookieSecure;

    @Value("${server.servlet.session.cookie.http-only:true}")
    private boolean sessionCookieHttpOnly;

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String accessToken = extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        // Bearer 헤더가 없으면 access_token 쿠키를 사용함.
        if (!StringUtils.hasText(accessToken)) {
            accessToken = extractCookie(request, ACCESS_COOKIE_NAME);
        }
        String refreshToken = extractCookie(request, REFRESH_COOKIE_NAME);

        log.info("[LOGOUT][REQ] 로그아웃 세션 시작 accessPresent={}, refreshPresent={}",
                StringUtils.hasText(accessToken), StringUtils.hasText(refreshToken));

        // 토큰 무효화 (멱등)
        logoutService.logout(accessToken, refreshToken);

        // token cookie와 session cookie를 각각 성격에 맞게 만료함.
        expireAccessCookie(response);
        expireRefreshCookie(response);

        // 시큐리티 컨텍스트/세션 정리 (인증되지 않은 상태여도 안전)
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        // JSESSIONID 세션 무효화
        expireSessionCookie(response);

        return ResponseEntity.ok(LOGOUT_RESPONSE);
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null) return null;
        String prefix = "Bearer ";
        if (authorizationHeader.startsWith(prefix)) {
            String token = authorizationHeader.substring(prefix.length());
            return (token != null ? token.trim() : null);
        }
        return null;
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]);
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private void expireAccessCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, expireAuthCookie(ACCESS_COOKIE_NAME).toString());
    }

    private void expireRefreshCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, expireAuthCookie(REFRESH_COOKIE_NAME).toString());
    }

    private ResponseCookie expireAuthCookie(String name) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(authCookieSecure)
                .sameSite(authCookieSameSite)
                .path("/")
                .maxAge(0);

        if (StringUtils.hasText(authCookieDomain)) {
            builder.domain(authCookieDomain);
        }

        return builder.build();
    }

    private void expireSessionCookie(HttpServletResponse response) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(sessionCookieName, "")
                .maxAge(0)
                .path(sessionCookiePath);

        if (sessionCookieHttpOnly) b = b.httpOnly(true);
        if (sessionCookieSecure)   b = b.secure(true);
        if (StringUtils.hasText(sessionCookieSameSite)) b = b.sameSite(sessionCookieSameSite);
        if (StringUtils.hasText(sessionCookieDomain))   b = b.domain(sessionCookieDomain);

        response.addHeader(HttpHeaders.SET_COOKIE, b.build().toString());
    }
}
