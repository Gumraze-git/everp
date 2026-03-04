package org.ever._4ever_be_gw.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SseHttpHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // SSE 엔드포인트인 경우
        if (httpRequest.getRequestURI().contains("/notifications/subscribe")) {

            log.debug(
                "[SSE][RESP-HEADERS-SETTING] Content-Type={}, Cache-Control={}, Connection={}, X-Accel-Buffering=no",
                httpResponse.getContentType(), httpResponse.getHeader(HttpHeaders.CACHE_CONTROL),
                httpResponse.getHeader(HttpHeaders.CONNECTION));

            // 스트리밍 헤더 직접 설정
            httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE);
            httpResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
            httpResponse.setHeader(HttpHeaders.CONNECTION, "keep-alive");
            httpResponse.setHeader("X-Accel-Buffering", "no"); // Nginx 버퍼링 방지

            // Content-Length 설정을 무시하는 Wrapper 사용
            HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(httpResponse) {
                @Override
                public void setContentLength(int len) {
                    // Content-Length 설정 무시
                }

                @Override
                public void setContentLengthLong(long len) {
                    // Content-Length 설정 무시
                }

                @Override
                public void setHeader(String name, String value) {
                    if (!"Content-Length".equalsIgnoreCase(name)) {
                        super.setHeader(name, value);
                    }
                }

                @Override
                public void addHeader(String name, String value) {
                    if (!"Content-Length".equalsIgnoreCase(name)) {
                        super.addHeader(name, value);
                    }
                }
            };

            chain.doFilter(request, wrapper);
        } else {
            chain.doFilter(request, response);
        }
    }
}
