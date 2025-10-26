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

    // ===== attributes (인가 요청/컨텍스트 값 — redirect_uri, state 등 포함 가능) =====
    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "attributes", columnDefinition = "TEXT")
    private Map<String, Object> attributes;

    @Column(name = "state", length = 200)
    private String state;

    // ===== Authorization Code =====
    @Lob
    @Column(name = "authorization_code_value")
    private String authorizationCodeValue;

    @Column(name = "authorization_code_issued_at")
    private Instant authorizationCodeIssuedAt;

    @Column(name = "authorization_code_expires_at")
    private Instant authorizationCodeExpiresAt;

    // code_challenge, code_challenge_method 등은 여기 메타데이터(JSON)에 저장됩니다.
    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "authorization_code_metadata", columnDefinition = "TEXT")
    private Map<String, Object> authorizationCodeMetadata;

    // ===== Access Token =====
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
    private String accessTokenType; // "Bearer" 등

    @Convert(converter = StringSetToJsonConverter.class)
    @Column(name = "access_token_scopes", columnDefinition = "TEXT")
    private Set<String> accessTokenScopes;

    // ===== Refresh Token =====
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