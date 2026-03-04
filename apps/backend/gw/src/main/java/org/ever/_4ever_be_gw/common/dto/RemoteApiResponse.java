package org.ever._4ever_be_gw.common.dto;

/**
 * 외부 서비스 표준 응답 래퍼를 역직렬화하기 위한 DTO.
 *
 * @param <T> data payload 타입
 */
public class RemoteApiResponse<T> {

    private int status;
    private boolean success;
    private String message;
    private T data;
    private Object errors;

    public RemoteApiResponse() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
