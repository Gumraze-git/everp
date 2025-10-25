package org.ever._4ever_be_auth.auth.oauth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ever._4ever_be_auth.common.jpa_converter.MapToJsonConverter;
import org.ever._4ever_be_auth.common.jpa_converter.StringSetToJsonConverter;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Getter
@Entity
@Table(name = "oauth2_authorization")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OAuth2AuthorizationEntity {
    @Id
    @Column(length = 100, nullable = false)
    private String id;

    @Column(name = "registered_client_id", length = 100, nullable = false)
    private String registeredClientId;

    @Column(name = "principal_name", length = 200, nullable = false)
    private String principalName;

    @Column(name = "authorization_grant_type", length = 100, nullable = false)
    private String authorizationGrantType;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "attributes", columnDefinition = "TEXT")
    private Map<String, Object> attributes;

    @Column(name = "state", length = 200)
    private String state;

    // Authorization Code(인가 코드)
    @Lob
    @Column(name = "authorization_code_value")
    private String authorizationCodeValue;

    @Column(name = "authorization_code_issued_at")
    private Instant authorizationCodeIssuedAt;

    @Column(name = "authorization_code_expires_at")
    private Instant authorizationCodeExpiresAt;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "authorization_code_metadata", columnDefinition = "TEXT")
    private Map<String, Object> authorizationCodeMetadata;

    // Access Token
    @Lob
    @Column(name = "access_token_value")
    private String accessTokenValue;
    @Column(name = "access_token_issued_at")
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at")
    private Instant accessTokenExpiresAt;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "access_token_metadata", columnDefinition = "TEXT")
    private Map<String, Object> accessTokenMetadata;

    @Column(name = "access_token_type", length = 100)
    private String accessTokenType;

    @Convert(converter = StringSetToJsonConverter.class)
    @Column(name = "access_token_scopes", columnDefinition = "TEXT")
    private Set<String> accessTokenScopes;

    // RefreshToken
    @Lob
    @Column(name = "refresh_token_value")
    private String refreshTokenValue;
    @Column(name = "refresh_token_issued_at")
    private Instant refreshTokenIssuedAt;
    @Column(name = "refresh_token_expires_at")
    private Instant refreshTokenExpiresAt;
    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "refresh_token_metadata", columnDefinition = "TEXT")
    private Map<String, Object> refreshTokenMetadata;
}
