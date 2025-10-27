package org.ever._4ever_be_auth.config.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
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
    @Bean(name = "plainJsonObjectMapper")
    public ObjectMapper plainJsonObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);

        // 메서드가 있으면 호출되고, 없으면 컴파일/런타임 문제 없이 무시
        try {
            om.deactivateDefaultTyping();
        } catch (NoSuchMethodError | Exception ignore) {
            // Jackson 버전에 따라 메서드가 없을 수 있음 → 무시
        }
        return om;
    }
}