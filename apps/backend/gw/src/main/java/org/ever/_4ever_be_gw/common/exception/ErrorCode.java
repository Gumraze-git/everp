package org.ever._4ever_be_gw.common.exception;

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
    INVALID_PERIODS(1007, HttpStatus.BAD_REQUEST, "요청 파라미터 'periods' 값이 올바르지 않습니다."),
    VALIDATION_FAILED(1008, HttpStatus.UNPROCESSABLE_ENTITY, "요청 파라미터 검증에 실패했습니다."),
    FORBIDDEN_RANGE(1009, HttpStatus.FORBIDDEN, "해당 범위의 데이터를 조회할 권한이 없습니다."),
    PERIOD_CALCULATION_FAILED(1010, HttpStatus.UNPROCESSABLE_ENTITY, "요청을 처리할 수 없습니다. 기간 계산 중 오류가 발생했습니다."),
    FORBIDDEN_PURCHASE_ACCESS(1011, HttpStatus.FORBIDDEN, "해당 구매요청서를 조회할 권한이 없습니다."),
    PURCHASE_REQUEST_NOT_FOUND(1012, HttpStatus.NOT_FOUND, "해당 구매요청서를 찾을 수 없습니다."),
    FORBIDDEN_DATA_ACCESS(1013, HttpStatus.FORBIDDEN, "해당 데이터를 조회할 권한이 없습니다."),
    UNKNOWN_PROCESSING_ERROR(1014, HttpStatus.INTERNAL_SERVER_ERROR, "요청 처리 중 알 수 없는 오류가 발생했습니다."),
    VENDOR_FORBIDDEN(1016, HttpStatus.FORBIDDEN, "공급업체 조회 권한이 없습니다."),
    VENDOR_NOT_FOUND(1017, HttpStatus.NOT_FOUND, "해당 공급업체를 찾을 수 없습니다."),
    VENDOR_PROCESSING_ERROR(1018, HttpStatus.INTERNAL_SERVER_ERROR, "공급업체 조회 처리 중 오류가 발생했습니다."),
    VENDOR_CREATE_FORBIDDEN(1019, HttpStatus.FORBIDDEN, "공급업체 등록 권한이 없습니다."),
    VENDOR_CREATE_VALIDATION_FAILED(1020, HttpStatus.UNPROCESSABLE_ENTITY, "요청 파라미터 검증에 실패했습니다."),
    VENDOR_CREATE_PROCESSING_ERROR(1021, HttpStatus.INTERNAL_SERVER_ERROR, "공급업체 등록 처리 중 오류가 발생했습니다."),
    VENDOR_ACCOUNT_FORBIDDEN(1022, HttpStatus.FORBIDDEN, "계정 생성 권한이 없습니다."),
    VENDOR_ACCOUNT_ALREADY_EXISTS(1023, HttpStatus.CONFLICT, "이미 계정이 발급된 공급업체입니다."),
    VENDOR_ACCOUNT_PROCESSING_ERROR(1024, HttpStatus.INTERNAL_SERVER_ERROR, "초대 이메일 발송 중 오류가 발생했습니다."),
    VENDOR_UPDATE_FORBIDDEN(1025, HttpStatus.FORBIDDEN, "공급업체 수정 권한이 없습니다."),
    VENDOR_UPDATE_VALIDATION_FAILED(1026, HttpStatus.UNPROCESSABLE_ENTITY, "요청 본문 검증에 실패했습니다."),
    VENDOR_UPDATE_PROCESSING_ERROR(1027, HttpStatus.INTERNAL_SERVER_ERROR, "공급업체 정보 수정 처리 중 오류가 발생했습니다."),
    VENDOR_UPDATE_NOT_FOUND(1028, HttpStatus.NOT_FOUND, "수정할 공급업체를 찾을 수 없습니다."),
    QUOTATION_DUE_DATE_INVALID(1019, HttpStatus.BAD_REQUEST, "요청 납기일은 현재 날짜 이후여야 합니다."),
    QUOTATION_ITEMS_EMPTY(1020, HttpStatus.UNPROCESSABLE_ENTITY, "items는 1개 이상이어야 합니다."),
    PURCHASE_ORDER_NOT_FOUND(1015, HttpStatus.NOT_FOUND, "해당 발주서를 찾을 수 없습니다."),
    QUOTATION_LIST_PROCESSING_ERROR(1021, HttpStatus.INTERNAL_SERVER_ERROR, "견적 목록 조회 처리 중 서버 오류가 발생했습니다."),
    QUOTATION_NOT_FOUND(1022, HttpStatus.NOT_FOUND, "해당 견적을 찾을 수 없습니다."),
    QUOTATION_FORBIDDEN(1023, HttpStatus.FORBIDDEN, "견적 상세를 조회할 권한이 없습니다."),
    QUOTATION_CONFIRM_INVALID_STATE(1024, HttpStatus.BAD_REQUEST, "요청한 견적 중 검토 요청이 불가능한 상태가 포함되어 있습니다."),
    QUOTATION_CONFIRM_NOT_FOUND(1025, HttpStatus.NOT_FOUND, "존재하지 않는 견적이 포함되어 있습니다."),
    CUSTOMER_REQUIRED_FIELDS_MISSING(1026, HttpStatus.BAD_REQUEST, "필수 필드가 누락되었습니다."),
    CUSTOMER_NOT_FOUND(1027, HttpStatus.NOT_FOUND, "고객사를 찾을 수 없습니다."),
    CUSTOMER_FORBIDDEN(1028, HttpStatus.FORBIDDEN, "해당 고객사를 조회할 권한이 없습니다."),
    CUSTOMER_UPDATE_FORBIDDEN(1029, HttpStatus.FORBIDDEN, "해당 고객사를 수정할 권한이 없습니다."),
    CUSTOMER_DELETE_FORBIDDEN(1030, HttpStatus.FORBIDDEN, "해당 고객사를 삭제할 권한이 없습니다."),
    CUSTOMER_DELETE_CONFLICT(1031, HttpStatus.CONFLICT, "해당 고객사는 거래 내역이 존재하여 삭제할 수 없습니다."),
    ANALYTICS_RANGE_TOO_LARGE(1032, HttpStatus.BAD_REQUEST, "조회 기간은 최대 6개월까지만 가능합니다."),
    AUTH_TOKEN_REQUIRED(1033, HttpStatus.UNAUTHORIZED, "유효한 인증 토큰이 필요합니다."),
    BODY_VALIDATION_FAILED(1034, HttpStatus.UNPROCESSABLE_ENTITY, "요청 본문 검증에 실패했습니다."),
    ANALYTICS_START_AFTER_END(1048, HttpStatus.CONFLICT, "startDate는 endDate보다 이후일 수 없습니다."),
    ANALYTICS_INVALID_DATE_FORMAT(1049, HttpStatus.UNPROCESSABLE_ENTITY, "날짜 형식이 올바르지 않습니다. YYYY-MM-DD 형식을 사용하세요."),
    PURCHASE_REQUEST_UPDATE_CONFLICT(1035, HttpStatus.CONFLICT, "현재 상태에서는 수정이 허용되지 않습니다. (required: NON_STOCK & PENDING)"),
    PURCHASE_REQUEST_APPROVAL_FORBIDDEN(1036, HttpStatus.FORBIDDEN, "승인 권한이 없습니다. (required role: PR_APPROVER|PURCHASING_MANAGER|ADMIN)"),
    PURCHASE_REQUEST_INVALID_TRANSITION(1037, HttpStatus.UNPROCESSABLE_ENTITY, "해당 상태에서는 승인할 수 없습니다."),
    PURCHASE_REQUEST_APPROVAL_PROCESSING_ERROR(1038, HttpStatus.INTERNAL_SERVER_ERROR, "요청 처리 중 오류가 발생했습니다."),
    PURCHASE_REQUEST_REJECTION_FORBIDDEN(1039, HttpStatus.FORBIDDEN, "해당 문서를 반려할 권한이 없습니다. (required role: PR_APPROVER|PURCHASING_MANAGER|ADMIN)"),
    PURCHASE_REQUEST_REJECTION_INVALID_TRANSITION(1040, HttpStatus.UNPROCESSABLE_ENTITY, "해당 상태에서는 반려할 수 없습니다."),
    PURCHASE_REQUEST_REJECTION_PROCESSING_ERROR(1041, HttpStatus.INTERNAL_SERVER_ERROR, "요청 처리 중 오류가 발생했습니다."),
    PURCHASE_REQUEST_DELETE_CONFLICT(1050, HttpStatus.CONFLICT, "대기 상태인 구매요청서만 삭제할 수 있습니다."),

    // Purchase Order Approval (PO)
    PURCHASE_ORDER_APPROVAL_FORBIDDEN(1042, HttpStatus.FORBIDDEN, "발주서 승인 권한이 없습니다."),
    PURCHASE_ORDER_INVALID_TRANSITION(1043, HttpStatus.CONFLICT, "현재 상태에서는 승인할 수 없습니다. (status=PENDING만 승인 가능)"),
    PURCHASE_ORDER_APPROVAL_PROCESSING_ERROR(1044, HttpStatus.INTERNAL_SERVER_ERROR, "승인 처리 중 오류가 발생했습니다."),

    // Purchase Order Rejection (PO)
    PURCHASE_ORDER_REJECTION_FORBIDDEN(1045, HttpStatus.FORBIDDEN, "발주서 반려 권한이 없습니다."),
    PURCHASE_ORDER_REJECTION_REASON_REQUIRED(1046, HttpStatus.UNPROCESSABLE_ENTITY, "반려 사유를 입력해야 합니다."),
    PURCHASE_ORDER_REJECTION_PROCESSING_ERROR(1047, HttpStatus.INTERNAL_SERVER_ERROR, "발주서 반려 처리 중 오류가 발생했습니다."),

    // User Errors (3000~3999)
    USER_NOT_FOUND(3000, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(3001, HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    INVALID_CREDENTIALS(3002, HttpStatus.UNAUTHORIZED, "잘못된 인증 정보입니다."),
    USER_ACCOUNT_LOCKED(3003, HttpStatus.FORBIDDEN, "계정이 잠겨있습니다."),

    // HR Errors (2000~2999)
    EMPLOYEE_NOT_FOUND(2000, HttpStatus.NOT_FOUND, "해당 직원을 찾을 수 없습니다."),
    EMPLOYEE_ALREADY_EXISTS(2001, HttpStatus.CONFLICT, "이미 존재하는 직원입니다."),
    DEPARTMENT_NOT_FOUND(2002, HttpStatus.NOT_FOUND, "해당 부서를 찾을 수 없습니다."),
    DEPARTMENT_ALREADY_EXISTS(2003, HttpStatus.CONFLICT, "이미 존재하는 부서입니다."),
    POSITION_NOT_FOUND(2004, HttpStatus.NOT_FOUND, "해당 직급을 찾을 수 없습니다."),
    POSITION_ALREADY_EXISTS(2005, HttpStatus.CONFLICT, "이미 존재하는 직급입니다."),
    ATTENDANCE_NOT_FOUND(2006, HttpStatus.NOT_FOUND, "해당 출퇴근 기록을 찾을 수 없습니다."),
    LEAVE_REQUEST_NOT_FOUND(2007, HttpStatus.NOT_FOUND, "해당 휴가 신청을 찾을 수 없습니다."),
    PAYROLL_NOT_FOUND(2008, HttpStatus.NOT_FOUND, "해당 급여 정보를 찾을 수 없습니다."),
    INVALID_ATTENDANCE_TIME(2009, HttpStatus.BAD_REQUEST, "잘못된 출퇴근 시간입니다."),
    INVALID_LEAVE_PERIOD(2010, HttpStatus.BAD_REQUEST, "잘못된 휴가 기간입니다."),
    INSUFFICIENT_LEAVE_DAYS(2011, HttpStatus.BAD_REQUEST, "휴가 일수가 부족합니다."),
    PAYROLL_ALREADY_PROCESSED(2012, HttpStatus.CONFLICT, "이미 처리된 급여입니다."),

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
    INVALID_STATE_TRANSITION(6002, HttpStatus.BAD_REQUEST, "유효하지 않은 상태 전환입니다."),
    EXTERNAL_API_ERROR(6003, HttpStatus.BAD_REQUEST , "비즈니스 서버 응답 형식이 올바르지 않습니다.");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
