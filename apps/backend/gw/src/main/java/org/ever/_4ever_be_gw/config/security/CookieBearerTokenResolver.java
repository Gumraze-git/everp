package org.ever._4ever_be_gw.config.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    @Override
    public String resolve(HttpServletRequest request) {
        // 브라우저 SPA는 access_token 쿠키를 사용함.
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()) && StringUtils.hasText(cookie.getValue())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
