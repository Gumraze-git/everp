package org.ever._4ever_be_gw.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final int status;
    private final boolean success;
    private final String message;
    private final T data;
    private final Object errors;

    /**
     * 성공 응답
     */
    public static <T> ApiResponse<T> success(T data, String message, HttpStatus status) {
        return new ApiResponse<>(
            status.value(),
            true,
            message,
            data,
            null
        );
    }

    /**
     * 실패 응답 (단일 메서드)
     * errors에 검증/에러 상세를 담고, data는 항상 null로 유지합니다.
     */
    public static <T> ApiResponse<T> fail(String message, HttpStatus status, Object errors) {
        return new ApiResponse<>(
            status.value(),
            false,
            message,
            null,
            errors
        );
    }
}
