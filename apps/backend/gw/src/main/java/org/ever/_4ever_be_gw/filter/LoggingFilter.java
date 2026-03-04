package org.ever._4ever_be_gw.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

// Request per 단위 Filter

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static final long MAX_LOG_LENGTH = 1000;

    public String maskSensitiveData(String requestBody) {
        return requestBody.replaceAll("\"password\"\\s*:\\s*\"(.*?)\"", "\"password\":\"****\"");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
        FilterChain filterChain) throws ServletException, IOException {

        // ⭐ SSE 엔드포인트는 로깅 필터 제외
        String requestURI = req.getRequestURI();
        if (requestURI.contains("/notifications/subscribe")) {
            log.debug("[LoggingFilter] SSE endpoint detected, skipping logging: {}", requestURI);
            filterChain.doFilter(req, res);
            return;
        }

        long srt = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedReq = new ContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper wrappedRes = new ContentCachingResponseWrapper(res);

        String response = "응답 본문: (empty)";
        int status = 0;

        try {
            filterChain.doFilter(wrappedReq, wrappedRes);
        } catch (Exception e) {
            log.warn("로깅 처리 중 에러 발생", e);
        } finally {
            String requestBody = new String(wrappedReq.getContentAsByteArray(),
                StandardCharsets.UTF_8).trim();

            if (!requestBody.isEmpty()) {
                requestBody = maskSensitiveData(requestBody);
                log.info(">>> 요청 본문: {}", requestBody);
            }

            status = wrappedRes.getStatus();
            byte[] contentAsByteArray = wrappedRes.getContentAsByteArray();
            if (contentAsByteArray.length > 0) {
                String responseBody = new String(contentAsByteArray, StandardCharsets.UTF_8).trim();

                // response body가 너무 크면 skip
                if (responseBody.length() > MAX_LOG_LENGTH) {
                    response = "응답 본문: 너무 커서 생략됨";
                } else {
                    response = "응답 본문: " + responseBody;
                }
                wrappedRes.copyBodyToResponse(); // 캐시된 응답 본문을 실제 응답에 복사
            } else {
                wrappedRes.copyBodyToResponse();
            }
        }

        log.info("\n"
                + "HTTP 메서드: [ {} ] 엔드포인트: [ {} ] Status: {} Content-Type: [ {} ] Authorization: [ {} ] User-agent: [ {} ] Host: [ {} ] Content-length: [ {} ] 응답 본문: [ {} ]"
            , req.getMethod(), req.getRequestURI(),
            status,
            req.getHeader("content-type"),
            req.getHeader("authorization"),
            req.getHeader("member-agent"),
            req.getHeader("host"),
            req.getHeader("content-length"),
            response
        );

        long end = System.currentTimeMillis();
        log.info(">>> 소요 시간: {} sec", (end - srt) / 1000.0);
    }


}
