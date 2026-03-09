package org.ever._4ever_be_gw.config.restclient;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.ever._4ever_be_gw.config.webclient.ApiProperties;

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
public class RestClientConfig {

    @Bean
    @Qualifier("restClientBuilder")
    public RestClient.Builder restClientBuilder() {
        return createBuilder(Duration.ofSeconds(5), Duration.ofSeconds(10));
    }

    @Bean
    @Qualifier("longTimeoutRestClientBuilder")
    public RestClient.Builder longTimeoutRestClientBuilder() {
        return createBuilder(Duration.ofSeconds(10), Duration.ofSeconds(60));
    }

    private RestClient.Builder createBuilder(Duration connectTimeout, Duration readTimeout) {
        HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(connectTimeout)
            .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
            .requestFactory(requestFactory)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    }
}
