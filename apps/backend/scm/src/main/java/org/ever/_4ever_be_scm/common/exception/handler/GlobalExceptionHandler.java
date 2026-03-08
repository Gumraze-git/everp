package org.ever._4ever_be_scm.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_scm.common.exception.BusinessException;
import org.ever._4ever_be_scm.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ProblemDetail> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("비즈니스 예외 발생: code={}, message={}, detail={}",
            e.getErrorCode().getCode(), e.getMessage(), e.getDetail(), e);
        ProblemDetail problemDetail = ProblemDetailFactory.fromBusinessException(e, request);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(problemDetail);
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

        ProblemDetail problemDetail = ProblemDetailFactory.badRequest(
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            "요청 본문 형식이 올바르지 않습니다.",
            errors,
            request,
            ErrorCode.INVALID_INPUT_VALUE.getCode()
        );
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
        ConstraintViolationException ex,
        HttpServletRequest request
    ) {
        log.error("제약 조건 위반 예외 발생: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetailFactory.badRequest(
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            "요청 본문 형식이 올바르지 않습니다.",
            ex.getConstraintViolations(),
            request,
            ErrorCode.INVALID_INPUT_VALUE.getCode()
        );
        return ResponseEntity.badRequest().body(problemDetail);
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

        ProblemDetail problemDetail = ProblemDetailFactory.badRequest(
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            errors,
            request,
            ErrorCode.INVALID_INPUT_VALUE.getCode()
        );
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e,
        HttpServletRequest request
    ) {
        log.error("메서드 인자 타입 불일치: {}", e.getMessage(), e);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("field", e.getName());
        errorDetails.put("rejectedValue", e.getValue());
        errorDetails.put("requiredType",
            e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");

        ProblemDetail problemDetail = ProblemDetailFactory.badRequest(
            ErrorCode.INVALID_TYPE_VALUE.getMessage(),
            ErrorCode.INVALID_TYPE_VALUE.getMessage(),
            errorDetails,
            request,
            ErrorCode.INVALID_TYPE_VALUE.getCode()
        );
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ProblemDetail> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e,
        HttpServletRequest request
    ) {
        log.error("필수 요청 파라미터 누락: {}", e.getMessage(), e);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("parameter", e.getParameterName());
        errorDetails.put("type", e.getParameterType());

        ProblemDetail problemDetail = ProblemDetailFactory.badRequest(
            ErrorCode.MISSING_INPUT_VALUE.getMessage(),
            ErrorCode.MISSING_INPUT_VALUE.getMessage(),
            errorDetails,
            request,
            ErrorCode.MISSING_INPUT_VALUE.getCode()
        );
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException e,
        HttpServletRequest request
    ) {
        log.error("HTTP 메시지 읽기 실패: {}", e.getMessage(), e);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("detail", "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요.");

        ProblemDetail problemDetail = ProblemDetailFactory.badRequest(
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            "요청 본문 형식이 올바르지 않습니다.",
            errorDetails,
            request,
            ErrorCode.INVALID_INPUT_VALUE.getCode()
        );
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ProblemDetail> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e,
        HttpServletRequest request
    ) {
        log.error("지원하지 않는 HTTP 메서드: {}", e.getMessage(), e);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("method", e.getMethod());
        errorDetails.put("supportedMethods", e.getSupportedHttpMethods());

        ProblemDetail problemDetail = ProblemDetailFactory.of(
            HttpStatus.METHOD_NOT_ALLOWED,
            ErrorCode.METHOD_NOT_ALLOWED.getMessage(),
            ErrorCode.METHOD_NOT_ALLOWED.getMessage(),
            errorDetails,
            request,
            ErrorCode.METHOD_NOT_ALLOWED.getCode()
        );
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(problemDetail);
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

        ProblemDetail problemDetail = ProblemDetailFactory.notFound(
            String.format("%s %s", e.getHttpMethod(), e.getRequestURL()),
            errors,
            request
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ProblemDetail> handleException(Exception e, HttpServletRequest request) {
        log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);
        ProblemDetail problemDetail = ProblemDetailFactory.serverError(
            request,
            ErrorCode.INTERNAL_SERVER_ERROR.getCode()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
