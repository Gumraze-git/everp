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

    // Spring Boot가 주입하는 기본 ObjectMapper (HTTP 시리얼라이저 등)
    private final ObjectMapper objectMapper;

    public SecurityJacksonConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 기본 ObjectMapper에도 Security/SAS 모듈을 등록해 두면
     * (로그/디버깅/HTTP 변환 시) 보안 타입을 안전하게 처리할 수 있습니다.
     */
    @PostConstruct
    void registerSecurityModulesOnDefaultMapper() {
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    /**
     * 일반 JSON 전용 (도메인 단순 Map, 설정값 등)
     * → 보안 타입 섞이지 않는 데이터에만 사용
     */
    @Bean(name = "plainJsonObjectMapper")
    public ObjectMapper plainJsonObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        // Jackson 버전에 따라 메서드 유무가 다를 수 있으므로 try/catch
        try {
            om.deactivateDefaultTyping();
        } catch (NoSuchMethodError | Exception ignore) { }
        return om;
    }

    /**
     * 보안 객체 직렬화/역직렬화 전용
     * → JPA 컨버터(예: SecurityMapToJsonConverter)에서 반드시 이 매퍼를 주입받아 사용
     *   (Authentication, OAuth2 tokens metadata 등 포함)
     */
    @Bean(name = "securityJsonObjectMapper")
    public ObjectMapper securityJsonObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        // Spring Security + Authorization Server 모듈
        om.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        om.registerModule(new OAuth2AuthorizationServerJackson2Module());

        // 시간 모듈 및 타임스탬프 옵션
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);

        // 기본 타입 활성화(Polymorphic default typing)는 절대 켜지지 않게 유지
        try {
            om.deactivateDefaultTyping();
        } catch (NoSuchMethodError | Exception ignore) { }

        return om;
    }
}
