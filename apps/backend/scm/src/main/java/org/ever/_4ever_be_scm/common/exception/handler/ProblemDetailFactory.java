package org.ever._4ever_be_scm.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.ever._4ever_be_scm.common.exception.BusinessException;
import org.ever._4ever_be_scm.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public final class ProblemDetailFactory {

    private static final String PROBLEM_BASE_URI = "https://ever.dev/problems/";

    private ProblemDetailFactory() {
    }

    public static ProblemDetail fromBusinessException(BusinessException exception, HttpServletRequest request) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = create(
            errorCode.getHttpStatus(),
            errorCode.getMessage(),
            exception.getDetail() != null ? exception.getDetail() : errorCode.getMessage(),
            request,
            errorCode.getCode()
        );

        if (exception.getDetail() != null && !exception.getDetail().isBlank()) {
            problemDetail.setProperty("errors", Map.of("detail", exception.getDetail()));
        }

        return problemDetail;
    }

    public static ProblemDetail badRequest(
        String title,
        String detail,
        Object errors,
        HttpServletRequest request,
        Integer code
    ) {
        return createWithErrors(HttpStatus.BAD_REQUEST, title, detail, errors, request, code);
    }

    public static ProblemDetail serverError(HttpServletRequest request, Integer code) {
        return create(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
            "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
            request,
            code
        );
    }

    public static ProblemDetail notFound(String detail, Object errors, HttpServletRequest request) {
        return createWithErrors(
            HttpStatus.NOT_FOUND,
            "요청한 리소스를 찾을 수 없습니다.",
            detail,
            errors,
            request,
            null
        );
    }

    public static ProblemDetail of(
        HttpStatus status,
        String title,
        String detail,
        Object errors,
        HttpServletRequest request,
        Integer code
    ) {
        return createWithErrors(status, title, detail, errors, request, code);
    }

    private static ProblemDetail createWithErrors(
        HttpStatus status,
        String title,
        String detail,
        Object errors,
        HttpServletRequest request,
        Integer code
    ) {
        ProblemDetail problemDetail = create(status, title, detail, request, code);
        if (errors != null) {
            problemDetail.setProperty("errors", errors);
        }
        return problemDetail;
    }

    private static ProblemDetail create(
        HttpStatus status,
        String title,
        String detail,
        HttpServletRequest request,
        Integer code
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(PROBLEM_BASE_URI + (code != null ? code : status.value())));
        if (request != null) {
            problemDetail.setInstance(URI.create(request.getRequestURI()));
            problemDetail.setProperty("traceId", resolveTraceId(request));
        }
        if (code != null) {
            problemDetail.setProperty("code", code);
        }
        return problemDetail;
    }

    private static String resolveTraceId(HttpServletRequest request) {
        String requestId = request.getHeader("X-Request-Id");
        if (requestId != null && !requestId.isBlank()) {
            return requestId;
        }
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }
        Object existing = request.getAttribute("traceId");
        if (existing instanceof String value && !value.isBlank()) {
            return value;
        }
        String generated = UUID.randomUUID().toString();
        request.setAttribute("traceId", generated);
        return generated;
    }
}
