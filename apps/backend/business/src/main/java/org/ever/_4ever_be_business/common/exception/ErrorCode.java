package org.ever._4ever_be_business.common.exception;

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

    // Client Errors (2000~2999) - 고객사 관리
    CLIENT_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "고객사 정보를 찾을 수 없습니다."),
    CLIENT_ALREADY_EXISTS(2001, HttpStatus.CONFLICT, "이미 존재하는 고객사입니다."),
    CLIENT_DELETE_FAILED(2002, HttpStatus.BAD_REQUEST, "고객사 삭제에 실패했습니다."),
    CLIENT_UPDATE_FAILED(2003, HttpStatus.BAD_REQUEST, "고객사 정보 수정에 실패했습니다."),
    INVALID_CLIENT_STATUS(2004, HttpStatus.BAD_REQUEST, "유효하지 않은 고객사 상태입니다."),
    CUSTOMER_NOT_FOUND(2005, HttpStatus.NOT_FOUND, "고객사를 찾을 수 없습니다."),
    QUOTATION_NOT_FOUND(2006, HttpStatus.NOT_FOUND, "견적을 찾을 수 없습니다."),
    ORDER_NOT_FOUND(2007, HttpStatus.NOT_FOUND, "주문서를 찾을 수 없습니다."),
    QUOTATION_APPROVAL_NOT_FOUND(2008, HttpStatus.NOT_FOUND, "견적 승인 정보를 찾을 수 없습니다."),
    CUSTOMER_COMPANY_NOT_FOUND(2009, HttpStatus.NOT_FOUND, "고객사 회사 정보를 찾을 수 없습니다."),

    // Contract Errors (3000~3999) - 계약 관리
    CONTRACT_NOT_FOUND(3000, HttpStatus.NOT_FOUND, "계약 정보를 찾을 수 없습니다."),
    CONTRACT_ALREADY_EXISTS(3001, HttpStatus.CONFLICT, "이미 존재하는 계약입니다."),
    CONTRACT_EXPIRED(3002, HttpStatus.BAD_REQUEST, "만료된 계약입니다."),
    CONTRACT_UPDATE_FAILED(3003, HttpStatus.BAD_REQUEST, "계약 정보 수정에 실패했습니다."),
    INVALID_CONTRACT_PERIOD(3004, HttpStatus.BAD_REQUEST, "유효하지 않은 계약 기간입니다."),
    CONTRACT_ALREADY_ACTIVE(3005, HttpStatus.BAD_REQUEST, "이미 활성화된 계약입니다."),

    // Partner Errors (4000~4999) - 파트너사 관리
    PARTNER_NOT_FOUND(4000, HttpStatus.NOT_FOUND, "파트너사 정보를 찾을 수 없습니다."),
    PARTNER_ALREADY_EXISTS(4001, HttpStatus.CONFLICT, "이미 존재하는 파트너사입니다."),
    PARTNER_DELETE_FAILED(4002, HttpStatus.BAD_REQUEST, "파트너사 삭제에 실패했습니다."),

    // External Service Errors (5000~5999)
    EXTERNAL_SERVICE_ERROR(5000, HttpStatus.BAD_GATEWAY, "외부 서비스 연동 중 오류가 발생했습니다."),
    KAFKA_SEND_ERROR(5001, HttpStatus.INTERNAL_SERVER_ERROR, "Kafka 메시지 전송에 실패했습니다."),
    REDIS_CONNECTION_ERROR(5002, HttpStatus.INTERNAL_SERVER_ERROR, "Redis 연결에 실패했습니다."),
    DATABASE_ERROR(5003, HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),

    KAFKA_PRODUCER_ERROR(1234,HttpStatus.INTERNAL_SERVER_ERROR,"카프카 에러"),

    // Business Logic Errors (6000~6999)
    BUSINESS_LOGIC_ERROR(6000, HttpStatus.BAD_REQUEST, "비즈니스 로직 처리 중 오류가 발생했습니다."),
    DUPLICATE_REQUEST(6001, HttpStatus.CONFLICT, "중복된 요청입니다."),
    INVALID_STATE_TRANSITION(6002, HttpStatus.BAD_REQUEST, "유효하지 않은 상태 전환입니다."),
    INVALID_BUSINESS_NUMBER(6003, HttpStatus.BAD_REQUEST, "유효하지 않은 사업자 번호입니다."),
    DOCUMENT_GENERATION_FAILED(6004, HttpStatus.INTERNAL_SERVER_ERROR, "문서 생성에 실패했습니다."),
    USER_NOT_FOUND(6010, HttpStatus.NOT_FOUND,"유저가 없습니다");


    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
