package org.ever._4ever_be_auth.common.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.ever._4ever_be_auth.common.exception.view.ErrorMessageResolver;
import org.ever._4ever_be_auth.common.exception.view.ErrorViewModel;
import org.ever._4ever_be_auth.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMessageResolver errorMessageResolver;

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected Object handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("비즈니스 예외 발생: code={}, message={}, detail={}",
            e.getErrorCode().getCode(), e.getMessage(), e.getDetail(), e);

        ErrorCode errorCode = e.getErrorCode();

        if (isHtmlRequest(request)) {
            return buildErrorView(errorCode, e.getDetail());
        }

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", errorCode.getCode());
        if (e.getDetail() != null) {
            errorDetails.put("detail", e.getDetail());
        }

        ApiResponse<Object> response = ApiResponse.fail(
            errorCode.getMessage(),
            errorCode.getHttpStatus(),
            errorDetails
        );

        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /**
     * @Valid 검증 실패 (MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("메서드 인자 검증 실패: {}", e.getMessage(), e);

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", ErrorCode.INVALID_INPUT_VALUE.getCode());
        errorDetails.put("errors", errors);

        ApiResponse<Object> response = ApiResponse.fail(
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            HttpStatus.BAD_REQUEST,
            errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * @ModelAttribute 검증 실패 (BindException)
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBindException(BindException e) {
        log.error("바인딩 예외 발생: {}", e.getMessage(), e);

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", ErrorCode.INVALID_INPUT_VALUE.getCode());
        errorDetails.put("errors", errors);

        ApiResponse<Object> response = ApiResponse.fail(
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            HttpStatus.BAD_REQUEST,
            errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 타입 불일치 (MethodArgumentTypeMismatchException)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("메서드 인자 타입 불일치: {}", e.getMessage(), e);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", ErrorCode.INVALID_TYPE_VALUE.getCode());
        errorDetails.put("field", e.getName());
        errorDetails.put("rejectedValue", e.getValue());
        errorDetails.put("requiredType", e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown");

        ApiResponse<Object> response = ApiResponse.fail(
            ErrorCode.INVALID_TYPE_VALUE.getMessage(),
            HttpStatus.BAD_REQUEST,
            errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 필수 파라미터 누락 (MissingServletRequestParameterException)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("필수 요청 파라미터 누락: {}", e.getMessage(), e);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", ErrorCode.MISSING_INPUT_VALUE.getCode());
        errorDetails.put("parameter", e.getParameterName());
        errorDetails.put("type", e.getParameterType());

        ApiResponse<Object> response = ApiResponse.fail(
            ErrorCode.MISSING_INPUT_VALUE.getMessage(),
            HttpStatus.BAD_REQUEST,
            errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * JSON 파싱 오류 (HttpMessageNotReadableException)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HTTP 메시지 읽기 실패: {}", e.getMessage(), e);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", ErrorCode.INVALID_INPUT_VALUE.getCode());
        errorDetails.put("detail", "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요.");

        ApiResponse<Object> response = ApiResponse.fail(
            ErrorCode.INVALID_INPUT_VALUE.getMessage(),
            HttpStatus.BAD_REQUEST,
            errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 지원하지 않는 HTTP 메서드 (HttpRequestMethodNotSupportedException)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("지원하지 않는 HTTP 메서드: {}", e.getMessage(), e);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", ErrorCode.METHOD_NOT_ALLOWED.getCode());
        errorDetails.put("method", e.getMethod());
        errorDetails.put("supportedMethods", e.getSupportedHttpMethods());

        ApiResponse<Object> response = ApiResponse.fail(
            ErrorCode.METHOD_NOT_ALLOWED.getMessage(),
            HttpStatus.METHOD_NOT_ALLOWED,
            errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 404 Not Found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected Object handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("핸들러를 찾을 수 없음: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            String detail = String.format("%s %s", e.getHttpMethod(), e.getRequestURL());
            return buildErrorView(ErrorCode.RESOURCE_NOT_FOUND, detail);
        }

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", 404);
        errorDetails.put("url", e.getRequestURL());
        errorDetails.put("method", e.getHttpMethod());

        ApiResponse<Object> response = ApiResponse.fail(
            "요청한 리소스를 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND,
            errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected Object handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        log.warn("정적 리소스를 찾을 수 없음: {}", e.getMessage());

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.RESOURCE_NOT_FOUND, e.getResourcePath());
        }

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", ErrorCode.RESOURCE_NOT_FOUND.getCode());
        errorDetails.put("resource", e.getResourcePath());

        ApiResponse<Object> response = ApiResponse.fail(
                "요청한 리소스를 찾을 수 없습니다.",
                HttpStatus.NOT_FOUND,
                errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * 모든 예외 처리 (Exception)
     */
    @ExceptionHandler(Exception.class)
    protected Object handleException(Exception e, HttpServletRequest request) {
        log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);

        if (isHtmlRequest(request)) {
            return buildErrorView(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        errorDetails.put("detail", e.getMessage());

        ApiResponse<Object> response = ApiResponse.fail(
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            errorDetails
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
        return accept == null && (!"XMLHttpRequest".equals(requestedWith));
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
