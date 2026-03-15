package org.ever._4ever_be_auth.auth.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2TokenResponseFailureHandler implements AuthenticationFailureHandler {

    // success handler와 같은 쿠키 속성을 쓰도록 설정값을 공유함.
    @Value("${everp.auth.cookie.domain:}")
    private String cookieDomain;

    @Value("${everp.auth.cookie.same-site:Lax}")
    private String cookieSameSite;

    @Value("${everp.auth.cookie.secure:true}")
    private boolean cookieSecure;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        // token endpoint 실패 시 access/refresh 쿠키를 함께 정리함.
        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie("access_token").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie("refresh_token").toString());
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
    }

    private ResponseCookie expireCookie(String name) {
        // 만료 쿠키도 성공 시점과 같은 속성으로 내려야 브라우저에서 정상 제거됨.
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(0);

        if (StringUtils.hasText(cookieDomain)) {
            builder.domain(cookieDomain);
        }

        return builder.build();
    }
}
