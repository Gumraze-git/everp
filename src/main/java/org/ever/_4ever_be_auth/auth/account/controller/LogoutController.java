package org.ever._4ever_be_auth.auth.account.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.service.LogoutService;
import org.ever._4ever_be_auth.common.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
public class LogoutController {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final LogoutService logoutService;

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String accessToken = extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        String refreshToken = extractCookie(request, REFRESH_COOKIE_NAME);

        log.info("[LOGOUT][REQ] accessPresent={}, refreshPresent={}",
                StringUtils.hasText(accessToken), StringUtils.hasText(refreshToken));

        // 토큰 무효화 (멱등)
        logoutService.logout(accessToken, refreshToken);

        // refresh 토큰 쿠키 만료 처리
        expireRefreshCookie(response);

        // 시큐리티 컨텍스트/세션 정리 (인증되지 않은 상태여도 안전)
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        ApiResponse<Map<String, Object>> body = ApiResponse.success(
                Map.of("success", true),
                "logged out",
                HttpStatus.OK
        );
        return ResponseEntity.ok(body);
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
}

