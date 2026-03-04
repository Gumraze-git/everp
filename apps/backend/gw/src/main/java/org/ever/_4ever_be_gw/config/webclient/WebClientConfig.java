package org.ever._4ever_be_gw.config.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
public class WebClientConfig {

    // WebClient 설정
    // 연결 시도 시간: 서버에 연결하는데 대기하는 시간
    // 응답 대기 시간: 연결 된 뒤 요청을 보낸 뒤 재응답을 기다리는 시간

    // 연결 타임아웃 설정: 5초
    // 응답 대기 시간: 10초
    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 연결 타임아웃 설정
            .responseTimeout(Duration.ofMillis(10000)) // 응답 대기 시간 설정
            .doOnConnected(conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
            );

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient)) // 공통 HttpClient 설정 적용
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 공통 헤더 설정
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                .maxInMemorySize(2 * 1024 * 1024)); // 최대 메모리 크기 설정 (예: 2MB)
    }

    // 연결 타임 아웃, 응답 대기 시간 설정을 완화한 WebClient Builder
    // 연결 타임 아웃: 10초
    // 응답 대기 시간: 60초
    @Bean
    public WebClient.Builder longTimeoutWebClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)   // 10초
                .responseTimeout(Duration.ofMinutes(60))                // 60초
                .doOnConnected(conn -> conn
                        // ReadTimeoutHandler: 연결이 성립 된 후, 서버로 부터의 응답 timeoud 시간 설정
                        .addHandlerLast(new ReadTimeoutHandler(60_000, TimeUnit.MILLISECONDS))
                        // WriteTimeoutHandler: 클라이언트가 서버로 요청 데이터를 보내는 동안 지정한 시간 안에 전송이 완료되지 않으면 시간 설정
                        .addHandlerLast(new WriteTimeoutHandler(60_000, TimeUnit.MILLISECONDS))
                );

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient)) // 공통 HttpClient 설정 적용
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 공통 헤더 설정
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs()
                .maxInMemorySize(2 * 1024 * 1024)); // 최대 메모리 크기 설정 (예: 2MB)
    }
}
