package org.ever._4ever_be_auth.auth.oauth.repository;

import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationConsentEntity;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationConsentEntity.OAuth2AuthorizationConsentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2AuthorizationConsentJpaRepository
        extends JpaRepository<OAuth2AuthorizationConsentEntity, OAuth2AuthorizationConsentId> {

    Optional<OAuth2AuthorizationConsentEntity> findByIdRegisteredClientIdAndIdPrincipalName(
            String registeredClientId,
            String principalName
    );
}
