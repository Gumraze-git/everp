package org.ever._4ever_be_business.common.dto.internel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내부 서버 간 통신용 공통 응답 포맷
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternelServerResponse<T> {
    private int status;
    private boolean success;
    private String message;
    private T data;
}
