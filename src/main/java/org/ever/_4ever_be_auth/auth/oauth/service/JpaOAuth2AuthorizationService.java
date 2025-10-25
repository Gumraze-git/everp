package org.ever._4ever_be_auth.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationEntity;
import org.ever._4ever_be_auth.auth.oauth.repository.OAuth2AuthorizationJpaRepository;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JpaOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final OAuth2AuthorizationMapper authorizationMapper;
    private final OAuth2AuthorizationJpaRepository authorizationRepository;

    @Override
    @Transactional
    public void save(OAuth2Authorization authorization) {
        OAuth2AuthorizationEntity entity = authorizationMapper.toEntity(authorization);
        authorizationRepository.save(entity);
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

        // tokenType이 null이면 "어떤 컬럼이든 맞는 것"을 찾는 포괄 검색
        if (tokenType == null) {
            result = findByAnyToken(token);
        } else {
            String value = tokenType.getValue(); // ex) "access_token", "refresh_token", "code", "id_token", "device_code", "user_code", "state"

            if (OAuth2TokenType.ACCESS_TOKEN.getValue().equals(value)) {
                result = authorizationRepository.findByAccessTokenValue(token);

            } else if (OAuth2TokenType.REFRESH_TOKEN.getValue().equals(value)) {
                result = authorizationRepository.findByRefreshTokenValue(token);

            } else if (org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CODE.equals(value)) {
                // 인가 코드(authorization code)
                result = authorizationRepository.findByAuthorizationCodeValue(token);

            } else if ("state".equals(value) || org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.STATE.equals(value)) {
                // 상태(state) 매칭이 필요한 시나리오가 있다면
                result = authorizationRepository.findByState(token);

            } else {
                // 알 수 없는 타입 → 포괄 검색으로 폴백
                result = findByAnyToken(token);
            }
        }

        return result.map(authorizationMapper::toDomain).orElse(null);
    }

    @Override
    @Transactional
    public void remove(OAuth2Authorization authorization) {
        authorizationRepository.deleteById(authorization.getId());
    }

    private Optional<OAuth2AuthorizationEntity> findByAnyToken(String token) {
        return authorizationRepository.findByState(token)
                .or(() -> authorizationRepository.findByAuthorizationCodeValue(token))
                .or(() -> authorizationRepository.findByAccessTokenValue(token))
                .or(() -> authorizationRepository.findByRefreshTokenValue(token));
    }
}
