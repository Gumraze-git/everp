package org.ever._4ever_be_gw.config.restclient;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.ApiProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class RestClientProvider {

    private final RestClient.Builder restClientBuilder;
    private final RestClient.Builder longTimeoutRestClientBuilder;
    private final ApiProperties apiProperties;

    private final Map<ApiClientKey, RestClient> cache = new ConcurrentHashMap<>();
    private final Map<ApiClientKey, RestClient> longTimeoutCache = new ConcurrentHashMap<>();

    public RestClientProvider(
        @Qualifier("restClientBuilder") RestClient.Builder restClientBuilder,
        @Qualifier("longTimeoutRestClientBuilder") RestClient.Builder longTimeoutRestClientBuilder,
        ApiProperties apiProperties
    ) {
        this.restClientBuilder = restClientBuilder;
        this.longTimeoutRestClientBuilder = longTimeoutRestClientBuilder;
        this.apiProperties = apiProperties;
    }

    public RestClient getRestClient(ApiClientKey clientKey) {
        Objects.requireNonNull(clientKey, "클라이언트 키(clientKey)는 null을 허용하지 않습니다.");
        return cache.computeIfAbsent(clientKey, key -> createClient(key, restClientBuilder));
    }

    public RestClient getLongTimeoutRestClient(ApiClientKey clientKey) {
        Objects.requireNonNull(clientKey, "클라이언트 키(clientKey)는 null을 허용하지 않습니다.");
        return longTimeoutCache.computeIfAbsent(clientKey, key -> createClient(key, longTimeoutRestClientBuilder));
    }

    private RestClient createClient(ApiClientKey clientKey, RestClient.Builder builder) {
        ApiProperties.ClientProperties clientProperties = apiProperties.getClients().get(clientKey.getPropertyKey());
        if (clientProperties == null) {
            log.error("RestClient 설정 없음: '{}'", clientKey.getPropertyKey());
            throw new IllegalArgumentException("API 클라이언트 키 설정이 잘못되었습니다. : " + clientKey);
        }

        return builder.clone()
            .baseUrl(clientProperties.getBaseUrl())
            .build();
    }
}
