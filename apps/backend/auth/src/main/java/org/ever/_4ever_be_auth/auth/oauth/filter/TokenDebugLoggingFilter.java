package org.ever._4ever_be_auth.auth.oauth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.service.TokenDebugInspector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TokenDebugLoggingFilter extends OncePerRequestFilter {

    private final TokenDebugInspector inspector;

    public TokenDebugLoggingFilter(TokenDebugInspector inspector) {
        this.inspector = inspector;
    }

    private static String preview(String s) {
        return (s == null || s.length() <= 10) ? s : s.substring(0, 10) + "...";
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        return !("/oauth2/token".equals(req.getRequestURI()) && "POST".equalsIgnoreCase(req.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String grant = req.getParameter("grant_type");
        String clientId = req.getParameter("client_id");
        String redirect = req.getParameter("redirect_uri");
        String code = req.getParameter("code");
        String verifier = req.getParameter("code_verifier");

        log.info("\n========== TOKEN REQUEST ==========\n" +
                        "[TOKEN][REQ] grant={}, client={}, redirect={}, code={}, verifier_present={}",
                grant, clientId, redirect, preview(code), verifier != null);

        if ("authorization_code".equals(grant)) {
            try {
                inspector.inspect(code, redirect, verifier);
            } catch (Throwable t) {
                log.warn("[TOKEN][REQ] inspector failed: {}", t.toString());
            }
        }
        chain.doFilter(req, res);
    }
}
