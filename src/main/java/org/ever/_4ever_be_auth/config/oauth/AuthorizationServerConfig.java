package org.ever._4ever_be_auth.config.oauth;

import com.github.f4b6a3.uuid.UuidCreator;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.ever._4ever_be_auth.auth.account.handler.LoginFailureHandler;
import org.ever._4ever_be_auth.auth.account.handler.LoginSuccessHandler;
import org.ever._4ever_be_auth.auth.client.filter.ClientValidationFilter;
import org.ever._4ever_be_auth.auth.client.service.ClientValidationService;
import org.ever._4ever_be_auth.auth.oauth.handler.RefreshTokenCookieAuthenticationFailureHandler;
import org.ever._4ever_be_auth.auth.oauth.handler.RefreshTokenCookieAuthenticationSuccessHandler;
import org.ever._4ever_be_auth.auth.oauth.repository.OAuth2AuthorizationConsentJpaRepository;
import org.ever._4ever_be_auth.auth.oauth.repository.OAuth2AuthorizationJpaRepository;
import org.ever._4ever_be_auth.auth.oauth.service.*;
import org.hibernate.id.uuid.UuidGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.UUID;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

// 하나 이상의 Bean 객체가 있는 경우의 각 Bean들을 인식하기 위해 등록하는 어노테이션
@Configuration
@EnableConfigurationProperties(TokenProperties.class)
public class AuthorizationServerConfig {
    /**
     * 민감값 프리뷰
     */
    private static String preview(String v) {
        if (v == null) return "null";
        int n = Math.min(10, v.length());
        return v.substring(0, n) + "...";
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurity(
            HttpSecurity http,
            LoginFailureHandler loginFailureHandler,
            LoginSuccessHandler loginSuccessHandler
    ) throws Exception {
        var handler = new CsrfTokenRequestAttributeHandler();
        http
                .securityMatcher("/login", "/error", "/css/**", "/js/**", "/images/**")
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(handler)
                        )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureHandler(loginFailureHandler)
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain resourceApi(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
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
    public RegisteredClientRepository registeredClientRepository(
            JpaRegisteredClientRepository repository,
            TokenSettings tokenSettings,
            PasswordEncoder passwordEncoder
    ) {
        RegisteredClient desired = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("everp")
            .clientSecret(passwordEncoder.encode("super-secret")) // 평문 대신 인코딩
            // 클라이언트 HTTP Basic 인증 방식 지정
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri("https://everp.co.kr/callback")
            .scope("erp.user.profile")
            .scope("offline_access")
            .tokenSettings(tokenSettings)
            .clientSettings(ClientSettings.builder()
                .requireProofKey(true)
                .requireAuthorizationConsent(false)
                .build())
            .build();

        // 2) 로컬 개발용 SPA(public) 클라이언트 추가
        RegisteredClient spa = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("everp-spa")
            // secret 제거 (public client)
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            // Authorization Code + PKCE
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:3000/callback")
            .scope("erp.user.profile")
            .scope("offline_access") // 필요 시 refresh token 발급
            .tokenSettings(tokenSettings)
            .clientSettings(ClientSettings.builder()
                .requireProofKey(true)              // PKCE 필수
                .requireAuthorizationConsent(false) // 동의 화면 필요 없으면 false
                .build())
            .build();

        // 저장/업데이트
        RegisteredClient existing = repository.findByClientId("everp");
        if (existing == null) repository.save(desired);
        else repository.save(RegisteredClient.from(existing)
            .clientSecret(passwordEncoder.encode("super-secret"))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .build());

        // spa 저장
        if (repository.findByClientId("everp-spa") == null) {
            repository.save(spa);
        }

        // ios
        RegisteredClient ios = RegisteredClient.withId(UuidCreator.getTimeOrdered().toString())
                .clientId("everp-ios")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("everp-ios://callback")
                .scope("erp.user.profile")
                .scope("offline_access") // 필요 시 refresh token 발급
                .tokenSettings(tokenSettings)
                .clientSettings(ClientSettings.builder()
                    .requireProofKey(true)              // PKCE 필수
                    .requireAuthorizationConsent(false) // 동의 화면 필요 없으면 false
                    .build())
                .build();

        // ios 저장
        if (repository.findByClientId("everp-ios") == null) {
            repository.save(ios);
        }

        // aos
        RegisteredClient aos = RegisteredClient.withId(UuidCreator.getTimeOrdered().toString())
                .clientId("everp-aos")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("everp-aos://callback")
                .scope("erp.user.profile")
                .scope("offline_access") // 필요 시 refresh token 발급
                .tokenSettings(tokenSettings)
                .clientSettings(ClientSettings.builder()
                    .requireProofKey(true)              // PKCE 필수
                    .requireAuthorizationConsent(false) // 동의 화면 필요 없으면 false
                    .build())
                .build();

        // aos 저장
        if (repository.findByClientId("everp-aos") == null) {
            repository.save(aos);
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
                        .accessTokenResponseHandler(
                                loggingTokenSuccessHandler(refreshTokenCookieAuthenticationSuccessHandler))
                        .errorResponseHandler(
                                loggingTokenFailureHandler(refreshTokenCookieAuthenticationFailureHandler))
                )
        );

        // Configurer가 노출한 표준 엔드포인트와 일반 보안 체인을 분리하기 위한 매처
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http.securityMatcher(endpointsMatcher)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/.well-known/jwks.json"
                        ).permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .requestCache(c -> {
                    // 인가 코드 플로우에서는 반드시 SavedRequest가 남도록 보장
                    HttpSessionRequestCache cache = new HttpSessionRequestCache();
                    cache.setRequestMatcher(req ->
                            "GET".equalsIgnoreCase(req.getMethod()) &&
                                    req.getRequestURI() != null &&
                                    req.getRequestURI().startsWith("/oauth2/authorize")
                    );
                    c.requestCache(cache);
                })
                .exceptionHandling(e -> e
                    .defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        // HTML 요청은 기존 규칙 유지
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                    )
                    .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new NegatedRequestMatcher(new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
                    )
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll());
        http.addFilterBefore(clientValidationFilter, UsernamePasswordAuthenticationFilter.class);

        // (JWT 검증은 별도 SecurityFilterChain에서 설정)

        return http.build();
    }

    /**
     * 토큰 응답 성공 로깅 래퍼
     */
    private AuthenticationSuccessHandler loggingTokenSuccessHandler(AuthenticationSuccessHandler delegate) {
        return (request, response, authentication) -> {
            // 요청 파라미터(원인 파악에 중요한 것 위주)
            String gt = request.getParameter(GRANT_TYPE);
            String cid = request.getParameter(CLIENT_ID);
            String redirect = request.getParameter(REDIRECT_URI);
            String code = request.getParameter(CODE);
            String verifier = request.getParameter("code_verifier"); // PkceParameterNames.CODE_VERIFIER 상수도 가능

            // 요약 로그
            // (성공 시에도 어떤 흐름으로 들어왔는지 남겨두면 이후 비교가 쉬움)
            org.slf4j.LoggerFactory.getLogger(getClass())
                    .info("[TOKEN][SUCCESS] grant_type={}, client_id={}, redirect_uri={}, code(10)={}, code_verifier(yn)={}",
                            gt, cid, redirect,
                            preview(code), (verifier != null && !verifier.isBlank()));

            // 기존 핸들러에 위임
            if (delegate != null) delegate.onAuthenticationSuccess(request, response, authentication);
        };
    }

    /**
     * 토큰 응답 실패 로깅 래퍼
     */
    private AuthenticationFailureHandler loggingTokenFailureHandler(AuthenticationFailureHandler delegate) {
        return (request, response, exception) -> {
            String gt = request.getParameter(GRANT_TYPE);
            String cid = request.getParameter(CLIENT_ID);
            String redirect = request.getParameter(REDIRECT_URI);
            String code = request.getParameter(CODE);
            String verifier = request.getParameter("code_verifier");
            String msg = (exception != null ? exception.getMessage() : "n/a");
            String ex = (exception != null ? exception.getClass().getSimpleName() : "n/a");

            // 중요 포인트: invalid_grant의 흔한 원인들을 한 줄에 묶어서 확인
            org.slf4j.LoggerFactory.getLogger(getClass())
                    .warn("[TOKEN][FAIL] {}: {} | grant_type={}, client_id={}, redirect_uri={}, code(10)={}, code_verifier(yn)={}, cookies={}",
                            ex, msg, gt, cid, redirect, preview(code),
                            (verifier != null && !verifier.isBlank()),
                            request.getHeader("Cookie"));

            // 추가로, 헤더/파라미터를 더 보고 싶으면 디버그 레벨로 남겨도 좋음
            if (org.slf4j.LoggerFactory.getLogger(getClass()).isDebugEnabled()) {
                var paramNames = request.getParameterMap().keySet();
                org.slf4j.LoggerFactory.getLogger(getClass())
                        .debug("[TOKEN][REQ] params={}", paramNames);
            }

            // 기존 핸들러에 위임
            if (delegate != null) delegate.onAuthenticationFailure(request, response, exception);
        };
    }

    // 예시: AuthorizationServerConfig 등 구성 클래스에 추가
    @Bean
    OAuth2TokenGenerator<OAuth2Token> tokenGenerator(
            JwtEncoder jwtEncoder,
            OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer
    ) {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(jwtCustomizer);
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(jwtGenerator, refreshTokenGenerator);
    }

    // 2) JwtEncoder 등록 (→ tokenGenerator에서 주입받음)
    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}
