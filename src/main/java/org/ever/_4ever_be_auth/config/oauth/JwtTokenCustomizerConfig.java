package org.ever._4ever_be_auth.config.oauth;

import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class JwtTokenCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(UserRepository userRepository) {
        return context -> {
            if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                return; // Access Token에만 적용
            }

            String username = null;
            if (context.getAuthorization() != null) {
                username = context.getAuthorization().getPrincipalName();
            }
            if (username == null && context.getPrincipal() != null) {
                username = context.getPrincipal().getName();
            }

            if (username == null || username.isBlank()) {
                return;
            }

            userRepository.findByLoginEmail(username).ifPresent(user -> {
                JwtClaimsSet.Builder claims = context.getClaims();
                claims.claim("user_id", user.getUserId().toString());
                claims.claim("role", user.getUserRole().name());

                Authentication principal = context.getPrincipal();
                if (principal != null && principal.getAuthorities() != null) {
                    Set<String> authorities = principal.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
                    if (!authorities.isEmpty()) {
                        claims.claim("authorities", authorities);
                    }
                }
            });
        };
    }
}

