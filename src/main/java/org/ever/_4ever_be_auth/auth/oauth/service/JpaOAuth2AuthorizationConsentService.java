package org.ever._4ever_be_auth.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationConsentEntity;
import org.ever._4ever_be_auth.auth.oauth.repository.OAuth2AuthorizationConsentJpaRepository;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JpaOAuth2AuthorizationConsentService implements OAuth2AuthorizationConsentService {
    private final OAuth2AuthorizationConsentMapper consentMapper;
    private final OAuth2AuthorizationConsentJpaRepository consentRepository;

    @Override
    @Transactional
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        consentRepository.save(consentMapper.toEntity(authorizationConsent));
    }

    @Override
    @Transactional
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        OAuth2AuthorizationConsentEntity.OAuth2AuthorizationConsentId id = OAuth2AuthorizationConsentEntity.OAuth2AuthorizationConsentId.builder()
                .registeredClientId(authorizationConsent.getRegisteredClientId())
                .principalName(authorizationConsent.getPrincipalName())
                .build();
        consentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        return consentRepository.findByIdRegisteredClientIdAndIdPrincipalName(registeredClientId, principalName)
                .map(consentMapper::toDomain)
                .orElse(null);
    }
}
