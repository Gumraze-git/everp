package org.ever._4ever_be_auth.config.security;

import org.ever._4ever_be_auth.auth.account.filter.PasswordChangeEnforcementFilter;
import org.ever._4ever_be_auth.auth.account.handler.LoginFailureHandler;
import org.ever._4ever_be_auth.auth.account.handler.LoginSuccessHandler;
import org.ever._4ever_be_auth.auth.client.filter.ClientValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            ClientValidationFilter clientValidationFilter,
            LoginFailureHandler loginFailureHandler,
            LoginSuccessHandler loginSuccessHandler,
            PasswordChangeEnforcementFilter passwordChangeEnforcementFilter
    ) throws Exception {
        // addFilterBefore = clientValidationFilter가 먼저 실행
        // 다음으로 UsernamePasswordAuthentication이 실행됨.
        // UserPasswordAuthentication: 사용자 이름과 비밀번호로 POST login 요청을 감시하며, authenticationManager에게 인증을 위임하여 수행함.
        http.addFilterBefore(clientValidationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(passwordChangeEnforcementFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/auth/health",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/.well-known/**",
                                // 로그인 페이지 및 정적자산 허용
                                "/css/**",
                                "/js/**",
                                "/login",
                                "/images/**",
                                "/favicon.ico",
                                "/videos/**",
                                // 비밀번호 찾기 url
                                "/password/reset",
                                "/password/reset/confirm",
                                "/password/change"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/auth/health"));

        http.formLogin(form -> form
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
