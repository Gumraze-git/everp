package org.ever._4ever_be_gw.config.application;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// 1. @ConfigurationProperties 를 통해 'services' prefix 의 속성을 이 클래스에 바인딩합니다.
// 2. @Configuration 어노테이션으로 이 클래스 자체도 Spring Bean으로 등록합니다.
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {

    // 3. YAML의 'gateway', 'auth' 등 동적인 키를 Map으로 받습니다.
    // Spring Boot가 자동으로 Map의 Key ('gateway')와 Value (ServiceInfo 객체)로 변환해줍니다.
    private final Map<String, ServiceInfo> map = new HashMap<>();

    // 4. 'name'과 'base-url'을 담을 내부 클래스(POJO)를 정의합니다.
    @Setter
    @Getter
    public static class ServiceInfo {

        // Getters and Setters
        private String name;
        private String baseUrl; // YAML의 'base-url'이 'baseUrl' (camelCase)로 자동 매핑됩니다.

    }

}