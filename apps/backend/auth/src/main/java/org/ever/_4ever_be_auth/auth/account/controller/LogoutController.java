package org.ever._4ever_be_auth.auth.account.controller;

import org.ever._4ever_be_auth.api.auth.LogoutApi;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.service.LogoutService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
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

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final Map<String, Object> LOGOUT_RESPONSE = Map.of("success", true);

    private final LogoutService logoutService;

    // ⬇⬇⬇ 세션 쿠키 속성을 설정에서 가져오도록(없으면 합리적 기본값)
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
        String refreshToken = extractCookie(request, REFRESH_COOKIE_NAME);

        log.info("[LOGOUT][REQ] 로그아웃 세션 시작 accessPresent={}, refreshPresent={}",
                StringUtils.hasText(accessToken), StringUtils.hasText(refreshToken));

        // 토큰 무효화 (멱등)
        logoutService.logout(accessToken, refreshToken);

        // refresh 토큰 쿠키 만료 처리
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

    private void expireRefreshCookie(HttpServletResponse response) {
        ResponseCookie expired = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, expired.toString());
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
