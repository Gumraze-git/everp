package org.ever._4ever_be_alarm.common.exception;

/**
 * 알람 관련 예외
 */
public class AlarmException extends BusinessException {

    public AlarmException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AlarmException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public AlarmException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public AlarmException(ErrorCode errorCode, String detail, Throwable cause) {
        super(errorCode, detail, cause);
    }
}
