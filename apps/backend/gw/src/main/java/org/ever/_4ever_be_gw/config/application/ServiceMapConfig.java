package org.ever._4ever_be_gw.config.application;

import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ServiceMapConfig {

    // 1. 2단계에서 만든 ServiceProperties Bean을 주입받습니다.
    private final ServiceProperties serviceProperties;

    // 2. [요청하신 Map] Service Name을 Key로, Base URL을 Value로 갖는 Map을 Bean으로 등록
    @Bean
    @Qualifier("serviceUrlByNameMap") // Bean의 이름을 지정 (여러 Map이 있을 경우 구분)
    public Map<String, String> serviceUrlByNameMap() {

        // ServiceProperties에 저장된 Map<String, ServiceInfo>를 Stream으로 변환
        return serviceProperties.getMap().values().stream()
            .collect(Collectors.toMap(
                ServiceProperties.ServiceInfo::getName,   // Key: "4EVER-Gateway"
                ServiceProperties.ServiceInfo::getBaseUrl // Value: "http://..."
            ));
    }

    // 3. [보너스] YAML의 키(gateway, auth)를 Key로, Base URL을 Value로 갖는 Map
    @Bean
    @Qualifier("serviceUrlByKeyMap")
    public Map<String, String> serviceUrlByKeyMap() {

        return serviceProperties.getMap().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey, // Key: "gateway"
                entry -> entry.getValue().getBaseUrl() // Value: "http://..."
            ));
    }
}