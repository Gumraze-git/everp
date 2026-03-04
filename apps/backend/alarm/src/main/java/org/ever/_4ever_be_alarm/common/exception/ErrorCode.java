package org.ever._4ever_be_alarm.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common Errors (1000~1999)
    INVALID_INPUT_VALUE(1000, HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(1001, HttpStatus.BAD_REQUEST, "잘못된 타입입니다."),
    MISSING_INPUT_VALUE(1002, HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다."),
    METHOD_NOT_ALLOWED(1003, HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    ACCESS_DENIED(1004, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(1005, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    UNAUTHORIZED(1006, HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

    // Payment Errors (2000~2999)
    PAYMENT_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ALREADY_COMPLETED(2001, HttpStatus.BAD_REQUEST, "이미 완료된 결제입니다."),
    PAYMENT_ALREADY_CANCELLED(2002, HttpStatus.BAD_REQUEST, "이미 취소된 결제입니다."),
    PAYMENT_AMOUNT_MISMATCH(2003, HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
    PAYMENT_FAILED(2004, HttpStatus.BAD_REQUEST, "결제 처리에 실패했습니다."),
    PAYMENT_CANCEL_FAILED(2005, HttpStatus.BAD_REQUEST, "결제 취소에 실패했습니다."),
    PAYMENT_TIMEOUT(2006, HttpStatus.REQUEST_TIMEOUT, "결제 처리 시간이 초과되었습니다."),
    INVALID_PAYMENT_METHOD(2007, HttpStatus.BAD_REQUEST, "유효하지 않은 결제 수단입니다."),
    INSUFFICIENT_BALANCE(2008, HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),

    // User Errors (3000~3999)
    USER_NOT_FOUND(3000, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(3001, HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    INVALID_CREDENTIALS(3002, HttpStatus.UNAUTHORIZED, "잘못된 인증 정보입니다."),
    USER_ACCOUNT_LOCKED(3003, HttpStatus.FORBIDDEN, "계정이 잠겨있습니다."),

    // Order Errors (4000~4999)
    ORDER_NOT_FOUND(4000, HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
    ORDER_ALREADY_COMPLETED(4001, HttpStatus.BAD_REQUEST, "이미 완료된 주문입니다."),
    ORDER_CANCELLED(4002, HttpStatus.BAD_REQUEST, "취소된 주문입니다."),

    // External Service Errors (5000~5999)
    EXTERNAL_SERVICE_ERROR(5000, HttpStatus.BAD_GATEWAY, "외부 서비스 연동 중 오류가 발생했습니다."),
    KAFKA_SEND_ERROR(5001, HttpStatus.INTERNAL_SERVER_ERROR, "Kafka 메시지 전송에 실패했습니다."),
    REDIS_CONNECTION_ERROR(5002, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 연결에 실패했습니다."),
    DATABASE_ERROR(5003, HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),

    // Business Logic Errors (6000~6999)
    BUSINESS_LOGIC_ERROR(6000, HttpStatus.BAD_REQUEST, "비즈니스 로직 처리 중 오류가 발생했습니다."),
    DUPLICATE_REQUEST(6001, HttpStatus.CONFLICT, "중복된 요청입니다."),
    INVALID_STATE_TRANSITION(6002, HttpStatus.BAD_REQUEST, "유효하지 않은 상태 전환입니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
