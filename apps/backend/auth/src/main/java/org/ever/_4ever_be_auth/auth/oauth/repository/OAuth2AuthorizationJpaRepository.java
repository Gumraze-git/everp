package org.ever._4ever_be_auth.auth.oauth.repository;

import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2AuthorizationJpaRepository extends JpaRepository<OAuth2AuthorizationEntity, String> {

    Optional<OAuth2AuthorizationEntity> findByState(String state);

    Optional<OAuth2AuthorizationEntity> findByAuthorizationCodeValue(String authorizationCodeValue);

    Optional<OAuth2AuthorizationEntity> findByAccessTokenValue(String accessTokenValue);

    Optional<OAuth2AuthorizationEntity> findByRefreshTokenValue(String refreshTokenValue);
}