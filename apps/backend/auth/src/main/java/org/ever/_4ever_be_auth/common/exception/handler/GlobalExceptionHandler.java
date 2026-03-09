package org.ever._4ever_be_auth.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.ever._4ever_be_auth.common.exception.view.ErrorMessageResolver;
import org.ever._4ever_be_auth.common.exception.view.ErrorViewModel;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMessageResolver errorMessageResolver;

    @ExceptionHandler(BusinessException.class)
    protected Object handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("비즈니스 예외 발생: code={}, message={}, detail={}",
            e.getErrorCode().getCode(), e.getMessage(), e.getDetail(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(e.getErrorCode(), e.getDetail());
        }

        return ResponseEntity
            .status(e.getErrorCode().getHttpStatus())
            .body(ProblemDetailFactory.fromBusinessException(e, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("메서드 인자 검증 실패: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.INVALID_INPUT_VALUE, e.getMessage());
        }

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(
            ProblemDetailFactory.badRequest(
                ErrorCode.INVALID_INPUT_VALUE.getMessage(),
                ErrorCode.INVALID_INPUT_VALUE.getMessage(),
                errors,
                request,
                ErrorCode.INVALID_INPUT_VALUE.getCode()
            )
        );
    }

    @ExceptionHandler(BindException.class)
    protected Object handleBindException(BindException e, HttpServletRequest request) {
        log.error("바인딩 예외 발생: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.INVALID_INPUT_VALUE, e.getMessage());
        }

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(
            ProblemDetailFactory.badRequest(
                ErrorCode.INVALID_INPUT_VALUE.getMessage(),
                ErrorCode.INVALID_INPUT_VALUE.getMessage(),
                errors,
                request,
                ErrorCode.INVALID_INPUT_VALUE.getCode()
            )
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected Object handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e,
        HttpServletRequest request
    ) {
        log.error("메서드 인자 타입 불일치: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.INVALID_TYPE_VALUE, e.getMessage());
        }

        Map<String, Object> errors = new HashMap<>();
        errors.put("field", e.getName());
        errors.put("rejectedValue", e.getValue());
        errors.put("requiredType", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");

        return ResponseEntity.badRequest().body(
            ProblemDetailFactory.badRequest(
                ErrorCode.INVALID_TYPE_VALUE.getMessage(),
                ErrorCode.INVALID_TYPE_VALUE.getMessage(),
                errors,
                request,
                ErrorCode.INVALID_TYPE_VALUE.getCode()
            )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected Object handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e,
        HttpServletRequest request
    ) {
        log.error("필수 요청 파라미터 누락: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.MISSING_INPUT_VALUE, e.getMessage());
        }

        Map<String, Object> errors = new HashMap<>();
        errors.put("parameter", e.getParameterName());
        errors.put("type", e.getParameterType());

        return ResponseEntity.badRequest().body(
            ProblemDetailFactory.badRequest(
                ErrorCode.MISSING_INPUT_VALUE.getMessage(),
                ErrorCode.MISSING_INPUT_VALUE.getMessage(),
                errors,
                request,
                ErrorCode.MISSING_INPUT_VALUE.getCode()
            )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected Object handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("HTTP 메시지 읽기 실패: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.INVALID_INPUT_VALUE, e.getMessage());
        }

        return ResponseEntity.badRequest().body(
            ProblemDetailFactory.badRequest(
                ErrorCode.INVALID_INPUT_VALUE.getMessage(),
                "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요.",
                null,
                request,
                ErrorCode.INVALID_INPUT_VALUE.getCode()
            )
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected Object handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e,
        HttpServletRequest request
    ) {
        log.error("지원하지 않는 HTTP 메서드: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.METHOD_NOT_ALLOWED, e.getMessage());
        }

        Map<String, Object> errors = new HashMap<>();
        errors.put("method", e.getMethod());
        errors.put("supportedMethods", e.getSupportedHttpMethods());

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            ProblemDetailFactory.of(
                HttpStatus.METHOD_NOT_ALLOWED,
                ErrorCode.METHOD_NOT_ALLOWED.getMessage(),
                ErrorCode.METHOD_NOT_ALLOWED.getMessage(),
                errors,
                request,
                ErrorCode.METHOD_NOT_ALLOWED.getCode()
            )
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected Object handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("핸들러를 찾을 수 없음: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.RESOURCE_NOT_FOUND, String.format("%s %s", e.getHttpMethod(), e.getRequestURL()));
        }

        Map<String, Object> errors = new HashMap<>();
        errors.put("url", e.getRequestURL());
        errors.put("method", e.getHttpMethod());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ProblemDetailFactory.notFound(
                "요청한 리소스를 찾을 수 없습니다.",
                errors,
                request,
                ErrorCode.RESOURCE_NOT_FOUND.getCode()
            )
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected Object handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        log.warn("정적 리소스를 찾을 수 없음: {}", e.getMessage());

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.RESOURCE_NOT_FOUND, e.getResourcePath());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ProblemDetailFactory.notFound(
                "요청한 리소스를 찾을 수 없습니다.",
                Map.of("resource", e.getResourcePath()),
                request,
                ErrorCode.RESOURCE_NOT_FOUND.getCode()
            )
        );
    }

    @ExceptionHandler(Exception.class)
    protected Object handleException(Exception e, HttpServletRequest request) {
        log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ProblemDetailFactory.serverError(
                "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                request,
                ErrorCode.INTERNAL_SERVER_ERROR.getCode()
            )
        );
    }

    private boolean isHtmlRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept != null) {
            if (accept.contains("text/html")) {
                return true;
            }
            if (accept.contains("application/xhtml+xml")) {
                return true;
            }
            if (accept.contains("application/json")) {
                return false;
            }
        }
        String requestedWith = request.getHeader("X-Requested-With");
        return accept == null && !"XMLHttpRequest".equals(requestedWith);
    }

    private ModelAndView buildErrorView(ErrorCode errorCode, String detail) {
        ErrorViewModel viewModel = errorMessageResolver.resolve(errorCode, detail);
        ModelAndView mav = new ModelAndView("error/general");
        mav.addObject("error", viewModel);
        mav.addObject("status", errorCode.getHttpStatus().value());
        mav.setStatus(errorCode.getHttpStatus());
        return mav;
    }
}
