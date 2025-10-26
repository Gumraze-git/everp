package org.ever._4ever_be_auth.auth.oauth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationEntity;
import org.ever._4ever_be_auth.auth.oauth.repository.OAuth2AuthorizationJpaRepository;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TokenDebugLoggingFilter extends OncePerRequestFilter {

    private final OAuth2AuthorizationJpaRepository repo;

    public TokenDebugLoggingFilter(OAuth2AuthorizationJpaRepository repo) {
        this.repo = repo;
    }

    private static String preview(String s) {
        return (s == null || s.length() <= 10) ? s : s.substring(0, 10) + "...";
    }

    private static String s256(String v) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(md.digest(v.getBytes(StandardCharsets.US_ASCII)));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        return !("/oauth2/token".equals(req.getRequestURI())
                && "POST".equalsIgnoreCase(req.getMethod()));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String grant = req.getParameter("grant_type");
        String clientId = req.getParameter("client_id");
        String redirect = req.getParameter("redirect_uri");
        String code = req.getParameter("code");
        String verifier = req.getParameter("code_verifier");

        log.info("\n========== TOKEN REQUEST ==========\n" +
                        "[TOKEN][REQ] grant={}, client={}, redirect={}, code={}, verifier_present={}",
                grant, clientId, redirect, preview(code), verifier != null);

        repo.findByAuthorizationCodeValue(code)
                .ifPresentOrElse(e -> logDetails(e, redirect, verifier),
                        () -> log.warn("[TOKEN][FAIL] code not found: {}", preview(code)));

        chain.doFilter(req, res);
    }

    private void logDetails(OAuth2AuthorizationEntity e, String suppliedRedirect, String suppliedVerifier) {
        var meta = e.getAuthorizationCodeMetadata();
        String storedRedirect = (String) meta.get("redirect_uri");
        String storedChallenge = (String) meta.get("code_challenge");
        String storedMethod = (String) meta.get("code_challenge_method");

        boolean redirectMatch = Objects.equals(storedRedirect, suppliedRedirect);
        String computed = suppliedVerifier != null ? s256(suppliedVerifier) : null;
        boolean pkceMatch = Objects.equals(storedChallenge, computed);

        log.info("[TOKEN][FOUND] id={}, principal={}", e.getId(), e.getPrincipalName());
        log.info("[TOKEN][CHECK][REDIRECT] stored={}, supplied={}, match={}",
                storedRedirect, suppliedRedirect, redirectMatch);
        log.info("[TOKEN][CHECK][PKCE] method={}, stored_challenge={}, computed_challenge={}, match={}",
                storedMethod, storedChallenge, computed, pkceMatch);

        if (!redirectMatch) log.warn("[TOKEN][FAIL] redirect mismatch → invalid_grant");
        if (!pkceMatch) log.warn("[TOKEN][FAIL] PKCE mismatch → invalid_grant");
    }
}
