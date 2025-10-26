package org.ever._4ever_be_auth.config.oauth;

import org.ever._4ever_be_auth.auth.account.handler.LoginFailureHandler;
import org.ever._4ever_be_auth.auth.account.handler.LoginSuccessHandler;
import org.ever._4ever_be_auth.auth.client.filter.ClientValidationFilter;
import org.ever._4ever_be_auth.auth.client.service.ClientValidationService;
import org.ever._4ever_be_auth.auth.oauth.handler.RefreshTokenCookieAuthenticationFailureHandler;
import org.ever._4ever_be_auth.auth.oauth.handler.RefreshTokenCookieAuthenticationSuccessHandler;
import org.ever._4ever_be_auth.auth.oauth.repository.OAuth2AuthorizationConsentJpaRepository;
import org.ever._4ever_be_auth.auth.oauth.repository.OAuth2AuthorizationJpaRepository;
import org.ever._4ever_be_auth.auth.oauth.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.UUID;

// 하나 이상의 Bean 객체가 있는 경우의 각 Bean들을 인식하기 위해 등록하는 어노테이션
@Configuration
@EnableConfigurationProperties(TokenProperties.class)
public class AuthorizationServerConfig {
    /**
     * {@code authorizationServerSecurityFilterChain}:
     * <ul>
     *     <il>인가 서버 전용 보안 필터 체인을 만들어 스프링 Security에 등록함.</il>
     *     <il>즉, OAuth 2.0 인가 서버의 엔드포인트를 처리할 보안 규칙을 세팅하는 것임.</il>
     * </ul>
     *
     */
    // 메서드가 반환하는 객체를 스프링 컨테이너에 빈으로 등록하는 어노테이션
    @Bean
    @Order(1) // Bean 객체의 적용 순서를 지정하는 어노테이션
    public SecurityFilterChain authorizationServerSecurityFilterChain(
            HttpSecurity http,
            RegisteredClientRepository registeredClientRepository,
            ClientValidationFilter clientValidationFilter,
            OAuth2AuthorizationService authorizationService,
            OAuth2AuthorizationConsentService authorizationConsentService,
            RefreshTokenCookieAuthenticationSuccessHandler refreshTokenCookieAuthenticationSuccessHandler,
            RefreshTokenCookieAuthenticationFailureHandler refreshTokenCookieAuthenticationFailureHandler
    ) throws Exception {

        // RegisteredClientRepository 주입: JDBC 기반 등록 클라이언트가 애플리케이션 기동 시점에 초기화되도록 보장
        // Configurer 인스턴스를 생성해 인가/토큰/JWKS 등 표준 엔드포인트를 등록하고 OIDC(JWKS 포함)를 활성화
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.with(authorizationServerConfigurer, configurer -> configurer
                .registeredClientRepository(registeredClientRepository)
                .authorizationService(authorizationService)
                .authorizationConsentService(authorizationConsentService)
                .tokenEndpoint(endpoint -> endpoint
                        .accessTokenResponseHandler(refreshTokenCookieAuthenticationSuccessHandler)
                        .errorResponseHandler(refreshTokenCookieAuthenticationFailureHandler))
        );

        // Configurer가 노출한 표준 엔드포인트와 일반 보안 체인을 분리하기 위한 매처
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http.securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/.well-known/openid-configuration",
                                "/.well-known/jwks.json"
                        ).permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll());
        http.addFilterBefore(clientValidationFilter, UsernamePasswordAuthenticationFilter.class);

        // (JWT 검증은 별도 SecurityFilterChain에서 설정)

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(
            HttpSecurity http,
            ClientValidationFilter clientValidationFilter,
            LoginFailureHandler loginFailureHandler,
            LoginSuccessHandler loginSuccessHandler
    ) throws Exception {
        http
                .securityMatcher("/login", "/error", "/css/**", "/js/**", "/images/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(Customizer.withDefaults())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureHandler(loginFailureHandler)
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                );
        http.addFilterBefore(clientValidationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain resourceApi(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    // Spring Authorization Server가 내부에서 사용하는 설정 값들을 묶어둔 객체로
    // 서버가 자신을 식별하고 각 엔드포인트 경로를 어떻게 노출할지 결정하는 정도를 가지고 있음.
    @Bean
    public AuthorizationServerSettings authorizationServerSettings(
            @Value("${spring.security.oauth2.authorizationserver.issuer}") String issuer) {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .authorizationEndpoint("/oauth2/authorize")
                .tokenEndpoint("/oauth2/token")
                .jwkSetEndpoint("/.well-known/jwks.json")
                .build();
    }

    @Bean
    public TokenSettings tokenSettings(TokenProperties tokenProperties) {
        return TokenSettings.builder()
                .accessTokenTimeToLive(tokenProperties.getAccessTokenTtl())
                .refreshTokenTimeToLive(tokenProperties.getRefreshTokenTtl())
                .reuseRefreshTokens(false)
                .build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JpaRegisteredClientRepository repository,
                                                                 TokenSettings tokenSettings) {
        RegisteredClient erpWebClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("everp")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://everp.co.kr/callback")
                .scope("erp.user.profile")      // 접근 권한 설정
                .tokenSettings(tokenSettings)
                .clientSettings(ClientSettings.builder().
                        requireProofKey(true). // PKCE
                                build())
                .build();

        if (repository.findByClientId(erpWebClient.getClientId()) == null) {
            repository.save(erpWebClient);
        }

        return repository;
    }

    @Bean
    public ClientValidationFilter clientValidationFilter(ClientValidationService clientValidationService) {
        return new ClientValidationFilter(clientValidationService);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(
            OAuth2AuthorizationMapper authorizationMapper,
            OAuth2AuthorizationJpaRepository authorizationRepository
    ) {
        return new JpaOAuth2AuthorizationService(authorizationMapper, authorizationRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            OAuth2AuthorizationConsentMapper consentMapper,
            OAuth2AuthorizationConsentJpaRepository consentRepository
    ) {
        return new JpaOAuth2AuthorizationConsentService(consentMapper, consentRepository);
    }

}
