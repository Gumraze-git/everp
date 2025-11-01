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
            // 액세스 토큰만 커스터마이징함.
            if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                return;
            }

            // 사용자 이름 결정 로직
            String username = null;
            if (context.getAuthorization() != null) {
                username = context.getAuthorization().getPrincipalName();
            }
            if (username == null && context.getPrincipal() != null) {
                username = context.getPrincipal().getName();
            }

            if (username == null || username.isBlank()) {
                log.debug("[DEBUG][JWT] 사용자 이름이 비어 있습니다.; skip claim enrichment");
                return;
            }

            final String principalUsername = username;

            userRepository.findByLoginEmail(principalUsername).ifPresent(user -> {
                JwtClaimsSet.Builder claims = context.getClaims();

                // 표준/일반 호환 키와 스네이크/카멜 케이스 모두 제공
                String userId = user.getUserId();
                String loginEmail = user.getLoginEmail();
                String role = user.getUserRole().name();
                String userType = user.getUserType().name();

                // subject를 고정 식별자로 설정(게이트웨이 호환성 향상)
                claims.subject(userId);

                // 도메인 전용(snake_case)
                claims.claim("user_id", userId);
                claims.claim("login_email", loginEmail);
                claims.claim("user_role", role);
                claims.claim("user_type", userType);

                log.info("[INFO][JWT] claims 클레임 정보: username={}, userId={}, role={}, userType={}",
                        principalUsername, userId, role, userType);
                    }
            );
        };
        }
}
}
