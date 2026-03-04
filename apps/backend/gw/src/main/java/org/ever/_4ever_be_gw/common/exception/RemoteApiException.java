package org.ever._4ever_be_gw.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 외부 서비스 호출 실패 시 상태/에러 정보를 함께 전달하기 위한 예외.
 */
public class RemoteApiException extends RuntimeException {

    private final HttpStatus status;
    private final Object errors;

    public RemoteApiException(HttpStatus status, String message, Object errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Object getErrors() {
        return errors;
    }
}
