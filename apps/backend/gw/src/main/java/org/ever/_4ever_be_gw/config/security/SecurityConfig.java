package org.ever._4ever_be_gw.config.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ever._4ever_be_gw.config.security.converter.EverJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
// 메서드 단위 보안 기능(@PreAuthorize, @PostAuthorize)를 활성화함.
// 따라서 컨트롤러나 서비스 메서드 위에서 권한을 검사할 수 있도록 함.
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final OAuth2ResourceServerProperties resourceServerProperties;
    private final String frontendOrigin;

    public SecurityConfig(
        OAuth2ResourceServerProperties resourceServerProperties,
        @Value("${EVERP_FRONTEND_ORIGIN:http://localhost:13000}") String frontendOrigin
    ) {
        this.resourceServerProperties = resourceServerProperties;
        this.frontendOrigin = frontendOrigin;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        CookieOrHeaderBearerTokenResolver bearerTokenResolver
    ) throws Exception {
        var handler = new CsrfTokenRequestAttributeHandler();
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                // 브라우저 SPA는 GW 전용 XSRF 쿠키와 헤더를 사용함.
                .csrfTokenRepository(cookieCsrfTokenRepository())
                .csrfTokenRequestHandler(handler)
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/csrf").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                // 외부 클라이언트 호환을 위해 Bearer 헤더는 유지하고, 브라우저는 쿠키 fallback을 허용함.
                .bearerTokenResolver(bearerTokenResolver)
                .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName("GW-XSRF-TOKEN");
        repository.setHeaderName("X-GW-XSRF-TOKEN");
        repository.setCookiePath("/");
        return repository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(buildAllowedOriginPatterns());
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "X-GW-XSRF-TOKEN"
        ));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private List<String> buildAllowedOriginPatterns() {
        List<String> origins = new ArrayList<>();
        origins.add(frontendOrigin);
        if (frontendOrigin.contains("://localhost:")) {
            origins.add(frontendOrigin.replace("://localhost:", "://127.0.0.1:"));
        }
        origins.add("https://api.everp.co.kr");
        origins.add("https://4-ever-fe.vercel.app");
        origins.add("https://everp.co.kr");
        return origins;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        OAuth2ResourceServerProperties.Jwt jwt = resourceServerProperties.getJwt();
        if (jwt == null || !StringUtils.hasText(jwt.getJwkSetUri())) {
            throw new IllegalStateException("spring.security.oauth2.resourceserver.jwt.jwk-set-uri must be configured");
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwt.getJwkSetUri()).build();

        if (StringUtils.hasText(jwt.getIssuerUri())) {
            OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(jwt.getIssuerUri());
            decoder.setJwtValidator(withIssuer);
        }

        return decoder;
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new EverJwtAuthenticationConverter();
    }
}
