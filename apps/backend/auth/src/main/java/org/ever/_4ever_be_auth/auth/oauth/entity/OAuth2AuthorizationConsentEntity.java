package org.ever._4ever_be_auth.auth.oauth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ever._4ever_be_auth.common.jpa_converter.StringSetToJsonConverter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Entity
@Table(name = "oauth2_authorization_consent")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OAuth2AuthorizationConsentEntity {
    @EmbeddedId
    private OAuth2AuthorizationConsentId id;

    @Convert(converter = StringSetToJsonConverter.class)
    @Column(name = "authorities", columnDefinition = "TEXT")
    private Set<String> authorities;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class OAuth2AuthorizationConsentId implements Serializable {
        @Column(name = "registered_client_id", length = 100, nullable = false)
        private String registeredClientId;

        @Column(name = "principal_name", length = 200, nullable = false)
        private String principalName;
    }
}