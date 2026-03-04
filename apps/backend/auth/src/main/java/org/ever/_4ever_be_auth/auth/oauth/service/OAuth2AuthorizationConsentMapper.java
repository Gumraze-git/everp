package org.ever._4ever_be_auth.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationConsentEntity;
import org.ever._4ever_be_auth.auth.oauth.entity.OAuth2AuthorizationConsentEntity.OAuth2AuthorizationConsentId;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OAuth2AuthorizationConsentMapper {

    private final RegisteredClientRepository registeredClientRepository;

    public OAuth2AuthorizationConsentEntity toEntity(OAuth2AuthorizationConsent consent) {
        return OAuth2AuthorizationConsentEntity.builder()
                .id(OAuth2AuthorizationConsentId.builder()
                        .registeredClientId(consent.getRegisteredClientId())
                        .principalName(consent.getPrincipalName())
                        .build())
                .authorities(consent.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .build();
    }

    public OAuth2AuthorizationConsent toDomain(OAuth2AuthorizationConsentEntity entity) {
        OAuth2AuthorizationConsentId id = entity.getId();

        RegisteredClient registeredClient = registeredClientRepository.findById(id.getRegisteredClientId());
        if (registeredClient == null) {
            throw new DataRetrievalFailureException(
                    "등록된 클라이언트를 찾을 수 없습니다. registeredClientId=" + id.getRegisteredClientId());
        }

        return OAuth2AuthorizationConsent.withId(id.getRegisteredClientId(),
                        id.getPrincipalName())
                .authorities(authorities -> {
                    authorities.clear();
                    if (entity.getAuthorities() != null) {
                        entity.getAuthorities().forEach(authority ->
                                authorities.add(new SimpleGrantedAuthority(authority)));
                    }
                })
                .build();
    }
}
