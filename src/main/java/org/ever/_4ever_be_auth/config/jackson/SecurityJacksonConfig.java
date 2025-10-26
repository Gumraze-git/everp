package org.ever._4ever_be_auth.config.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

@Configuration
public class SecurityJacksonConfig {

    private final ObjectMapper objectMapper;

    public SecurityJacksonConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    void registerSecurityModules() {
        // Spring Security 모듈
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        // Spring Authorization Server 모듈 (보강)
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    }
}