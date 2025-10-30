package org.ever._4ever_be_auth.config.oauth;

import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Slf4j
@Configuration
public class JwtTokenCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(UserRepository userRepository) {
        return context -> {
            if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                return; // Access Token에만 적용 (버전 호환)
            }

            String username = null;
            if (context.getAuthorization() != null) {
                username = context.getAuthorization().getPrincipalName();
            }
            if (username == null && context.getPrincipal() != null) {
                username = context.getPrincipal().getName();
            }

            if (username == null || username.isBlank()) {
                log.debug("[JWT-CUSTOMIZER] principal username is empty; skip claim enrichment");
                return;
            }

            final String principalUsername = username;

            userRepository.findByLoginEmail(principalUsername).ifPresentOrElse(user -> {
                JwtClaimsSet.Builder claims = context.getClaims();

                // 표준/일반 호환 키와 스네이크/카멜 케이스 모두 제공
                String userId = user.getUserId();
                String loginEmail = user.getLoginEmail();
                String role = user.getUserRole().name();
                String userType = user.getUserType().name();

                // subject를 고정 식별자로 설정(게이트웨이 호환성 향상)
                claims.subject(userId);

                // 표준/일반 필드
                claims.claim("email", loginEmail);
                claims.claim("preferred_username", loginEmail);
                claims.claim("roles", java.util.List.of("ROLE_" + role));

                // 도메인 전용(snake_case)
                claims.claim("user_id", userId);
                claims.claim("login_email", loginEmail);
                claims.claim("user_role", role);
                claims.claim("user_type", userType);

                log.info("[JWT] claims set for user: username={}, userId={}, role={}, userType={}",
                        principalUsername, userId, role, userType);
            }, () -> {
                // loginEmail로 조회되지 않을 경우, email 필드로도 조회 시도(운영 데이터 이행/정합성 대비)
                userRepository.findByEmail(principalUsername).ifPresent(u2 -> {
                    JwtClaimsSet.Builder claims = context.getClaims();
                    String userId = u2.getUserId();
                    String loginEmail = u2.getLoginEmail();
                    String role = u2.getUserRole().name();
                    String userType = u2.getUserType().name();

                    claims.subject(userId);
                    claims.claim("email", loginEmail);
                    claims.claim("preferred_username", loginEmail);
                    claims.claim("roles", java.util.List.of("ROLE_" + role));

                    claims.claim("user_id", userId);
                    claims.claim("login_email", loginEmail);
                    claims.claim("user_role", role);
                    claims.claim("user_type", userType);

                    log.info("[JWT-CUSTOMIZER] claims set via contact email: username={}, userId={}, role={}, userType={}",
                            principalUsername, userId, role, userType);
                });
            });
        };
    }
}
