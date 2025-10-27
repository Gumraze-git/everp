package org.ever._4ever_be_auth.auth.oauth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.ever._4ever_be_auth.common.jpa_converter.PlainMapToJsonConverter;
import org.ever._4ever_be_auth.common.jpa_converter.SecurityMapToJsonConverter;
import org.ever._4ever_be_auth.common.jpa_converter.StringSetToJsonConverter;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Getter
@Entity
@Table(
        name = "oauth2_authorization",
        indexes = {
                @Index(name = "idx_oauth2_auth_code_value", columnList = "authorization_code_value"),
                @Index(name = "idx_oauth2_access_token_value", columnList = "access_token_value"),
                @Index(name = "idx_oauth2_refresh_token_value", columnList = "refresh_token_value"),
                @Index(name = "idx_oauth2_state", columnList = "state"),
                @Index(name = "idx_oauth2_registered_client", columnList = "registered_client_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
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

    // ===== attributes (redirect_uri, state 등 포함 가능) =====
    @Convert(converter = SecurityMapToJsonConverter.class)
    @Column(name = "attributes", columnDefinition = "text")
    private Map<String, Object> attributes;

    @Column(name = "state", length = 512) // 넉넉히
    private String state;

    // ===== Authorization Code =====
    @Column(name = "authorization_code_value", columnDefinition = "text")
    private String authorizationCodeValue;

    @Column(name = "authorization_code_issued_at")
    private Instant authorizationCodeIssuedAt;

    @Column(name = "authorization_code_expires_at")
    private Instant authorizationCodeExpiresAt;

    @Convert(converter = PlainMapToJsonConverter.class)
    @Column(name = "authorization_code_metadata", columnDefinition = "text")
    private Map<String, Object> authorizationCodeMetadata;

    // ===== Access Token =====
    @Column(name = "access_token_value", columnDefinition = "text")
    private String accessTokenValue;

    @Column(name = "access_token_issued_at")
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at")
    private Instant accessTokenExpiresAt;

    @Convert(converter = PlainMapToJsonConverter.class)
    @Column(name = "access_token_metadata", columnDefinition = "text")
    private Map<String, Object> accessTokenMetadata;

    @Column(name = "access_token_type", length = 100)
    private String accessTokenType; // "Bearer"

    @Convert(converter = StringSetToJsonConverter.class)
    @Column(name = "access_token_scopes", columnDefinition = "text")
    private Set<String> accessTokenScopes;

    // ===== Refresh Token =====
    @Column(name = "refresh_token_value", columnDefinition = "text")
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at")
    private Instant refreshTokenIssuedAt;

    @Column(name = "refresh_token_expires_at")
    private Instant refreshTokenExpiresAt;

    @Convert(converter = PlainMapToJsonConverter.class)
    @Column(name = "refresh_token_metadata", columnDefinition = "text")
    private Map<String, Object> refreshTokenMetadata;
}
