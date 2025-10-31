package org.ever._4ever_be_auth.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final OAuth2AuthorizationService authorizationService;

    /**
     * 현재 세션과 연관된 Authorization을 안전하게 무효화한다.
     * - refresh token 우선, 그 다음 access token으로 조회
     * - 같은 Authorization을 중복 삭제하지 않도록 보호
     * - 조회 실패/없음은 무시(멱등)
     */
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        Map<String, OAuth2Authorization> toRemove = new LinkedHashMap<>();

        if (StringUtils.hasText(refreshToken)) {
            OAuth2Authorization byRefresh = authorizationService.findByToken(refreshToken, OAuth2TokenType.REFRESH_TOKEN);
            if (byRefresh != null) {
                toRemove.put(byRefresh.getId(), byRefresh);
            } else {
                log.debug("[LOGOUT] refresh token not found or already removed");
            }
        }

        if (StringUtils.hasText(accessToken)) {
            OAuth2Authorization byAccess = authorizationService.findByToken(accessToken, OAuth2TokenType.ACCESS_TOKEN);
            if (byAccess != null) {
                toRemove.putIfAbsent(byAccess.getId(), byAccess);
            } else {
                log.debug("[LOGOUT] access token not found or already removed");
            }
        }

        if (toRemove.isEmpty()) {
            log.info("[LOGOUT] no authorization found to remove (idempotent)");
            return;
        }

        toRemove.values().forEach(authz -> {
            try {
                authorizationService.remove(authz);
                log.info("[LOGOUT] authorization removed id={}, clientId={}, principal={}",
                        authz.getId(), authz.getRegisteredClientId(), authz.getPrincipalName());
            } catch (Exception e) {
                // 방어적: 이미 삭제된 경우 등 예외는 로그만 남기고 진행
                log.warn("[LOGOUT] failed to remove authorization id={}: {}", authz.getId(), e.getMessage());
            }
        });
    }
}

