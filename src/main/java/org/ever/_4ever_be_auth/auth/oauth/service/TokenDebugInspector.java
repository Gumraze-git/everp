package org.ever._4ever_be_auth.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.repository.OAuth2AuthorizationJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenDebugInspector {

    private final OAuth2AuthorizationJpaRepository repo;

    private static String s256(String v) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            return java.util.Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(md.digest(v.getBytes(java.nio.charset.StandardCharsets.US_ASCII)));
        } catch (Exception e) {
            return null;
        }
    }

    private static String preview(String s) {
        return (s == null || s.length() <= 12) ? s : s.substring(0, 12) + "...";
    }

    @Transactional(readOnly = true)
    public void inspect(String code, String suppliedRedirect, String suppliedVerifier) {
        repo.findByAuthorizationCodeValue(code).ifPresentOrElse(e -> {
            var meta = e.getAuthorizationCodeMetadata();
            String storedRedirect = (String) meta.get("redirect_uri");
            String storedChallenge = (String) meta.get("code_challenge");
            String storedMethod = (String) meta.get("code_challenge_method");

            boolean redirectMatch = java.util.Objects.equals(storedRedirect, suppliedRedirect);
            String computed = suppliedVerifier == null ? null : s256(suppliedVerifier);
            boolean pkceMatch = storedChallenge != null && storedChallenge.equals(computed);

            log.info("[TOKEN][FOUND] id={}, principal={}", e.getId(), e.getPrincipalName());
            log.info("[TOKEN][CHECK][REDIRECT] stored={}, supplied={}, match={}",
                    storedRedirect, suppliedRedirect, redirectMatch);
            log.info("[TOKEN][CHECK][PKCE] method={}, stored_challenge={}, computed_challenge={}, match={}",
                    storedMethod, storedChallenge, computed, pkceMatch);

            if (!redirectMatch) log.warn("[TOKEN][FAIL] redirect mismatch → invalid_grant");
            if (!pkceMatch) log.warn("[TOKEN][FAIL] PKCE mismatch → invalid_grant");
        }, () -> log.warn("[TOKEN][FAIL] code not found: {}", preview(code)));
    }
}

