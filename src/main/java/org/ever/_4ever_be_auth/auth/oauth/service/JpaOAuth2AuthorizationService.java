package org.ever._4ever_be_auth.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationEntity;
import org.ever._4ever_be_auth.auth.oauth.repository.OAuth2AuthorizationJpaRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaOAuth2AuthorizationService implements org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService {

    private final OAuth2AuthorizationMapper authorizationMapper;
    private final OAuth2AuthorizationJpaRepository authorizationRepository;

    @Override
    @Transactional
    public void save(OAuth2Authorization authorization) {
        // 공통 식별자
        String clientId = authorization.getRegisteredClientId();
        String principal = authorization.getPrincipalName();

        // create vs update 판단
        boolean exists = authorization.getId() != null && authorizationRepository.existsById(authorization.getId());

        // 전체 attributes 덤프
        Map<String, Object> attrs = authorization.getAttributes();
        log.info("[OAUTH][ATTR] keys={}, values={}", attrs.keySet(), attrs);

        // ===== 인가 요청 파라미터 로깅 (AUTHORIZATION_REQUEST) =====
        OAuth2AuthorizationRequest req =
                authorization.getAttribute("org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest");
        String redirectUri = null, state = null, codeChallenge = null, codeChallengeMethod = null;

        if (req != null) {
            var add = req.getAdditionalParameters();
            redirectUri = req.getRedirectUri();
            state = req.getState();
            codeChallenge = (String) add.get(PkceParameterNames.CODE_CHALLENGE);
            codeChallengeMethod = (String) add.get(PkceParameterNames.CODE_CHALLENGE_METHOD);

            log.info("[OAUTH][REQ] redirectUri={}, state={}, code_challenge={}, method={}",
                    redirectUri, state, codeChallenge, codeChallengeMethod);
        } else {
            log.warn("[OAUTH][REQ] OAuth2AuthorizationRequest attribute is NULL (will cause invalid_grant)");
        }

        // ===== 인가 코드(authorization_code) 메타데이터 보강 및 로깅 =====
        Token<OAuth2AuthorizationCode> codeToken = authorization.getToken(OAuth2AuthorizationCode.class);
        if (codeToken != null && codeToken.getToken() != null) {
            Map<String, Object> meta = codeToken.getMetadata();
            log.info("[OAUTH][CODE_META] keys={}, values={}", meta.keySet(), meta);

            // attrs 우선, 없으면 meta → 하지만 최종적으로 meta에도 채워 넣어 DB에 저장되게 한다.
            Object redirectUriEff = firstNonNull(attrs.get(OAuth2ParameterNames.REDIRECT_URI), meta.get(OAuth2ParameterNames.REDIRECT_URI), redirectUri);
            Object stateEff = firstNonNull(attrs.get(OAuth2ParameterNames.STATE), meta.get(OAuth2ParameterNames.STATE), state);
            Object codeChallengeEff = firstNonNull(attrs.get(PkceParameterNames.CODE_CHALLENGE), meta.get(PkceParameterNames.CODE_CHALLENGE), codeChallenge);
            Object codeChallengeMethodEff = firstNonNull(attrs.get(PkceParameterNames.CODE_CHALLENGE_METHOD), meta.get(PkceParameterNames.CODE_CHALLENGE_METHOD), codeChallengeMethod);

            boolean needEnrich =
                    (meta.get(OAuth2ParameterNames.REDIRECT_URI) == null && redirectUriEff != null) ||
                            (meta.get(OAuth2ParameterNames.STATE) == null && stateEff != null) ||
                            (meta.get(PkceParameterNames.CODE_CHALLENGE) == null && codeChallengeEff != null) ||
                            (meta.get(PkceParameterNames.CODE_CHALLENGE_METHOD) == null && codeChallengeMethodEff != null);

            if (needEnrich) {
                var code = codeToken.getToken();

                // 불변 객체라서 재빌드로 메타데이터 병합
                authorization = OAuth2Authorization.from(authorization)
                        .token(code, m -> {
                            m.putAll(meta);
                            if (redirectUriEff != null) m.put(OAuth2ParameterNames.REDIRECT_URI, redirectUriEff);
                            if (stateEff != null) m.put(OAuth2ParameterNames.STATE, stateEff);
                            if (codeChallengeEff != null) m.put(PkceParameterNames.CODE_CHALLENGE, codeChallengeEff);
                            if (codeChallengeMethodEff != null)
                                m.put(PkceParameterNames.CODE_CHALLENGE_METHOD, codeChallengeMethodEff);
                        })
                        .build();
            }

            // 재빌드 이후 최종 메타 확인/로그
            var codeAfter = authorization.getToken(OAuth2AuthorizationCode.class);
            if (codeAfter != null && codeAfter.getToken() != null) {
                var m2 = codeAfter.getMetadata();
                log.info("[OAUTH][CODE_META] (final) keys={}, values={}", m2.keySet(), m2);
                log.info(
                        "[OAUTH][CODE-ISSUED] 인가 코드 요청 시 로깅 clientId={}, principal={}, issuedAt={}, expiresAt={}, code={}, redirect_uri={}, state={}, code_challenge={}, method={}",
                        clientId, principal,
                        codeAfter.getToken().getIssuedAt(),
                        codeAfter.getToken().getExpiresAt(),
                        preview(codeAfter.getToken().getTokenValue()),
                        m2.get(OAuth2ParameterNames.REDIRECT_URI),
                        m2.get(OAuth2ParameterNames.STATE),
                        m2.get(PkceParameterNames.CODE_CHALLENGE),
                        m2.get(PkceParameterNames.CODE_CHALLENGE_METHOD)
                );
            }
        }

        // ===== 액세스 토큰 로깅 =====
        Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null && accessToken.getToken() != null) {
            var at = accessToken.getToken();
            log.info("[INFO][OAUTH][AT-ISSUED] 액세스 토큰 로깅 clientId={}, principal={}, tokenType={}, issuedAt={}, expiresAt={}, scopes={}, accessToken={}",
                    authorization.getRegisteredClientId(),
                    authorization.getPrincipalName(),
                    at.getTokenType() != null ? at.getTokenType().getValue() : "N/A",
                    at.getIssuedAt(),
                    at.getExpiresAt(),
                    at.getScopes(),
                    preview(at.getTokenValue())
            );
        }

        // ===== 리프레시 토큰 로깅 =====
        Token<OAuth2RefreshToken> refreshToken = authorization.getToken(OAuth2RefreshToken.class);
        if (refreshToken != null && refreshToken.getToken() != null) {
            var rt = refreshToken.getToken();
            log.info("[INFO][OAUTH][RT-ISSUED] 리프레시 토큰 로깅 clientId={}, principal={}, issuedAt={}, expiresAt={}, refreshToken={}",
                    authorization.getRegisteredClientId(),
                    authorization.getPrincipalName(),
                    rt.getIssuedAt(),
                    rt.getExpiresAt(),
                    preview(rt.getTokenValue())
            );
        }

        // ===== 저장 =====
        OAuth2AuthorizationEntity entity = authorizationMapper.toEntity(authorization);
        authorizationRepository.save(entity);

        log.debug("[INFO][OAUTH] {} id={}, clientId={}, principal={}",
                exists ? "updated" : "created",
                authorization.getId(),
                authorization.getRegisteredClientId(),
                authorization.getPrincipalName());
    }

    @Override
    @Transactional(readOnly = true)
    public OAuth2Authorization findById(String id) {
        return authorizationRepository.findById(id)
                .map(authorizationMapper::toDomain)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        if (token == null || token.isBlank()) {
            return null;
        }

        Optional<OAuth2AuthorizationEntity> result;

        if (tokenType == null) {
            result = findByAnyToken(token);
            if (result.isEmpty()) {
                log.debug("[INFO][OAUTH][LOOKUP] token(notype) not found, tokenPreview={}", preview(token));
            }
        } else {
            String value = tokenType.getValue(); // "access_token","refresh_token","code","state",...
            if (OAuth2TokenType.ACCESS_TOKEN.getValue().equals(value)) {
                result = authorizationRepository.findByAccessTokenValue(token);
            } else if (OAuth2TokenType.REFRESH_TOKEN.getValue().equals(value)) {
                result = authorizationRepository.findByRefreshTokenValue(token);
            } else if (OAuth2ParameterNames.CODE.equals(value) || "code".equals(value)) {
                result = authorizationRepository.findByAuthorizationCodeValue(token);
            } else if (OAuth2ParameterNames.STATE.equals(value) || "state".equals(value)) {
                result = authorizationRepository.findByState(token);
            } else {
                // 알 수 없는 타입 → 포괄 검색
                result = findByAnyToken(token);
            }

            if (result.isEmpty()) {
                log.debug("[INFO][OAUTH][LOOKUP] tokenType={}, tokenPreview={} not found in DB",
                        value, preview(token));
            }
        }
        return result.map(authorizationMapper::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public void remove(OAuth2Authorization authorization) {
        authorizationRepository.deleteById(authorization.getId());
        log.debug("[INFO][OAUTH] removed id={}, clientId={}, principal={}",
                authorization.getId(),
                authorization.getRegisteredClientId(),
                authorization.getPrincipalName());
    }

    private Optional<OAuth2AuthorizationEntity> findByAnyToken(String token) {
        return authorizationRepository.findByState(token)
                .or(() -> authorizationRepository.findByAuthorizationCodeValue(token))
                .or(() -> authorizationRepository.findByAccessTokenValue(token))
                .or(() -> authorizationRepository.findByRefreshTokenValue(token));
    }

    private String preview(String tokenValue) {
        if (tokenValue == null) return "null";
        int n = Math.min(tokenValue.length(), 10);
        return tokenValue.substring(0, n) + "...";
    }
}
