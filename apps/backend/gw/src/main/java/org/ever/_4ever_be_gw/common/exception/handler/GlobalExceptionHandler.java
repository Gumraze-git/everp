package org.ever._4ever_be_gw.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ProblemDetail> handleBusinessException(
        BusinessException e,
        HttpServletRequest request
    ) {
        log.error("비즈니스 예외 발생: code={}, message={}, detail={}",
            e.getErrorCode().getCode(), e.getMessage(), e.getDetail(), e);
        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ProblemDetailFactory.fromBusinessException(e, request));
    }

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<ProblemDetail> handleValidationException(
        ValidationException e,
        HttpServletRequest request
    ) {
        log.error("도메인 검증 실패: {}", e.getMessage(), e);
        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ProblemDetailFactory.of(
                e.getErrorCode().getHttpStatus(),
                e.getErrorCode().getMessage(),
                e.getMessage(),
                e.getErrors(),
                request,
                e.getErrorCode().getCode()
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e,
        HttpServletRequest request
    ) {
        log.error("메서드 인자 검증 실패: {}", e.getMessage(), e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
            .badRequest()
            .body(ProblemDetailFactory.badRequest(
                "요청 본문 형식이 올바르지 않습니다.",
                "요청 본문 형식이 올바르지 않습니다.",
                errors,
                request,
                ErrorCode.INVALID_INPUT_VALUE.getCode()
            ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleConstraintViolation(
        ConstraintViolationException ex,
        ServerWebExchange exchange
    ) {
        log.error("제약 조건 위반 예외 발생: {}", ex.getMessage(), ex);
        ProblemDetail problemDetail = ProblemDetailFactory.of(
            HttpStatus.BAD_REQUEST,
            "요청 본문 형식이 올바르지 않습니다.",
            "요청 본문 형식이 올바르지 않습니다.",
            ex.getConstraintViolations(),
            null,
            ErrorCode.INVALID_INPUT_VALUE.getCode()
        );
        problemDetail.setInstance(URI.create(exchange.getRequest().getURI().getPath()));
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problemDetail)
        );
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleWebClientResponseException(
        WebClientResponseException e,
        ServerWebExchange exchange
    ) {
        log.error("리액티브 HTTP 클라이언트 응답 예외 발생: {}", e.getMessage(), e);
        ProblemDetail problemDetail = ProblemDetailFactory.fromWebClientResponseException(e, "downstream");
        problemDetail.setInstance(URI.create(exchange.getRequest().getURI().getPath()));
        return Mono.just(
            ResponseEntity
                .status(HttpStatus.valueOf(e.getStatusCode().value()))
                .body(problemDetail)
        );
    }

    @ExceptionHandler(RestClientResponseException.class)
    protected ResponseEntity<ProblemDetail> handleRestClientResponseException(
        RestClientResponseException e,
        HttpServletRequest request
    ) {
        log.error("RestClient 응답 예외 발생: {}", e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.valueOf(e.getStatusCode().value()))
            .body(ProblemDetailFactory.fromRestClientResponseException(e, request, "downstream"));
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ProblemDetail> handleBindException(BindException e, HttpServletRequest request) {
        log.error("바인딩 예외 발생: {}", e.getMessage(), e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
            .badRequest()
            .body(ProblemDetailFactory.badRequest(
                ErrorCode.INVALID_INPUT_VALUE.getMessage(),
                ErrorCode.INVALID_INPUT_VALUE.getMessage(),
                errors,
                request,
                ErrorCode.INVALID_INPUT_VALUE.getCode()
            ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e,
        HttpServletRequest request
    ) {
        log.error("메서드 인자 타입 불일치: {}", e.getMessage(), e);
        Map<String, Object> errors = new HashMap<>();
        errors.put("field", e.getName());
        errors.put("rejectedValue", e.getValue());
        errors.put("requiredType",
            e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");
        return ResponseEntity
            .badRequest()
            .body(ProblemDetailFactory.badRequest(
                ErrorCode.INVALID_TYPE_VALUE.getMessage(),
                ErrorCode.INVALID_TYPE_VALUE.getMessage(),
                errors,
                request,
                ErrorCode.INVALID_TYPE_VALUE.getCode()
            ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ProblemDetail> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e,
        HttpServletRequest request
    ) {
        log.error("필수 요청 파라미터 누락: {}", e.getMessage(), e);
        Map<String, Object> errors = new HashMap<>();
        errors.put("parameter", e.getParameterName());
        errors.put("type", e.getParameterType());
        return ResponseEntity
            .badRequest()
            .body(ProblemDetailFactory.badRequest(
                ErrorCode.MISSING_INPUT_VALUE.getMessage(),
                ErrorCode.MISSING_INPUT_VALUE.getMessage(),
                errors,
                request,
                ErrorCode.MISSING_INPUT_VALUE.getCode()
            ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException e,
        HttpServletRequest request
    ) {
        log.error("HTTP 메시지 읽기 실패: {}", e.getMessage(), e);
        return ResponseEntity
            .badRequest()
            .body(ProblemDetailFactory.badRequest(
                "요청 본문 형식이 올바르지 않습니다.",
                "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요.",
                null,
                request,
                ErrorCode.INVALID_INPUT_VALUE.getCode()
            ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ProblemDetail> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e,
        HttpServletRequest request
    ) {
        log.error("지원하지 않는 HTTP 메서드: {}", e.getMessage(), e);
        Map<String, Object> errors = new HashMap<>();
        errors.put("method", e.getMethod());
        errors.put("supportedMethods", e.getSupportedHttpMethods());
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(ProblemDetailFactory.of(
                HttpStatus.METHOD_NOT_ALLOWED,
                ErrorCode.METHOD_NOT_ALLOWED.getMessage(),
                ErrorCode.METHOD_NOT_ALLOWED.getMessage(),
                errors,
                request,
                ErrorCode.METHOD_NOT_ALLOWED.getCode()
            ));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ProblemDetail> handleNoHandlerFoundException(
        NoHandlerFoundException e,
        HttpServletRequest request
    ) {
        log.error("핸들러를 찾을 수 없음: {}", e.getMessage(), e);
        Map<String, Object> errors = new HashMap<>();
        errors.put("url", e.getRequestURL());
        errors.put("method", e.getHttpMethod());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ProblemDetailFactory.notFound("요청한 리소스를 찾을 수 없습니다.", errors, request));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ProblemDetail> handleNoResourceFoundException(
        NoResourceFoundException e,
        HttpServletRequest request
    ) {
        log.error("정적 리소스를 찾을 수 없음: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ProblemDetailFactory.notFound(
                "요청한 리소스를 찾을 수 없습니다.",
                Map.of("resourcePath", e.getResourcePath()),
                request
            ));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<ProblemDetail> handleAuthorizationException(
        AuthorizationDeniedException e,
        HttpServletRequest request
    ) {
        log.error("[ERROR] 권한 거부: {}", e.getMessage());
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ProblemDetailFactory.of(
                HttpStatus.FORBIDDEN,
                "권한이 없습니다.",
                e.getMessage(),
                null,
                request,
                ErrorCode.ACCESS_DENIED.getCode()
            ));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ProblemDetail> handleException(Exception e, HttpServletRequest request) {
        log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ProblemDetailFactory.serverError(request, ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
    }
}
