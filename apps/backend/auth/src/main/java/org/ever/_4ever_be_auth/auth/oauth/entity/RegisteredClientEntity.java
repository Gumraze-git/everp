package org.ever._4ever_be_auth.auth.oauth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "oauth2_registered_client")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RegisteredClientEntity {

    @Id
    @Column(length = 100, nullable = false)
    private String id;

    @Column(name = "client_id", nullable = false, unique = true, length = 100)
    private String clientId;

    @Column(name = "client_id_issued_at")
    private LocalDateTime clientIdIssuedAt;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private LocalDateTime clientSecretExpiresAt;

    @Column(name = "client_name", nullable = false, length = 200)
    private String clientName;

    @Column(name = "authentication_methods", length = 1000)
    private String clientAuthenticationMethods;

    @Column(name = "authorization_grant_types", length = 1000)
    private String authorizationGrantTypes;

    @Column(name = "redirect_uris", length = 2000)
    private String redirectUris;

    @Column(name = "post_logout_redirect_uris", length = 2000)
    private String postLogoutRedirectUris;

    @Column(name = "scopes", length = 2000)
    private String scopes;

    @Column(name = "client_settings", columnDefinition = "TEXT")
    private String clientSettings;

    @Column(name = "token_settings", columnDefinition = "TEXT")
    private String tokenSettings;

    public void updateFrom(RegisteredClientEntity source) {
        this.clientId = source.clientId;
        this.clientIdIssuedAt = source.clientIdIssuedAt;
        this.clientSecret = source.clientSecret;
        this.clientSecretExpiresAt = source.clientSecretExpiresAt;
        this.clientName = source.clientName;
        this.clientAuthenticationMethods = source.clientAuthenticationMethods;
        this.authorizationGrantTypes = source.authorizationGrantTypes;
        this.redirectUris = source.redirectUris;
        this.postLogoutRedirectUris = source.postLogoutRedirectUris;
        this.scopes = source.scopes;
        this.clientSettings = source.clientSettings;
        this.tokenSettings = source.tokenSettings;
    }
}
