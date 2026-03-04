package org.ever._4ever_be_gw.business.dto.hrm;

/**
 * 인증 사용자 생성 결과 이벤트에서 필요한 필드를 전달하기 위한 DTO.
 */
public class CreateAuthUserResultDto {

    private String eventId;
    private String transactionId;
    private boolean success;
    private String userId;
    private String failureReason;

    public CreateAuthUserResultDto() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
