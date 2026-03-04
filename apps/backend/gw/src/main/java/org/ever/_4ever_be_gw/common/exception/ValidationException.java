package org.ever._4ever_be_gw.common.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ValidationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final List<Map<String, String>> errors;

    public ValidationException(ErrorCode errorCode, List<Map<String, String>> errors) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errors = errors;
    }
}

