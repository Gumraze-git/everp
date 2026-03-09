package org.ever._4ever_be_auth.user.application.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.ever._4ever_be_auth.common.exception.handler.ProblemDetailFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 비동기 결과 관리자 구현체
 *
 * @param <T> 결과 데이터 타입
 */
@Slf4j
@Component
public class GenericAsyncResultManager<T> implements AsyncResultManager<T> {

    private final Map<String, DeferredResult<ResponseEntity<?>>> pendingResults = new ConcurrentHashMap<>();

    @Override
    public void registerResult(String transactionId, DeferredResult<ResponseEntity<?>> result) {
        log.info("트랜잭션 {} 에 대한 DeferredResult 등록", transactionId);
        pendingResults.put(transactionId, result);

        result.onCompletion(() -> {
            log.info("트랜잭션 {} 완료, DeferredResult 맵에서 제거", transactionId);
            pendingResults.remove(transactionId);
        });
    }

    @Override
    public void setSuccessResult(String transactionId, T data) {
        setSuccessResult(transactionId, data, "처리가 완료되었습니다.", HttpStatus.OK);
    }

    @Override
    public void setSuccessResult(String transactionId, T data, String message, HttpStatus status) {
        DeferredResult<ResponseEntity<?>> result = pendingResults.get(transactionId);
        if (result != null && !result.isSetOrExpired()) {
            log.info("트랜잭션 {} 성공 결과 설정", transactionId);
            ResponseEntity<?> responseEntity = data != null
                ? ResponseEntity.status(status).body(data)
                : ResponseEntity.status(status).build();
            result.setResult(responseEntity);
        } else {
            log.warn("트랜잭션 {}에 대한 DeferredResult를 찾을 수 없거나 이미 완료됨", transactionId);
        }
    }

    @Override
    public void setErrorResult(String transactionId, String errorMessage, HttpStatus status) {
        DeferredResult<ResponseEntity<?>> result = pendingResults.get(transactionId);
        if (result != null && !result.isSetOrExpired()) {
            log.info("트랜잭션 {} 오류 결과 설정: {}", transactionId, errorMessage);
            ResponseEntity<?> responseEntity = ResponseEntity.status(status).body(
                ProblemDetailFactory.of(
                    status,
                    errorMessage,
                    errorMessage,
                    null,
                    null,
                    ErrorCode.INTERNAL_SERVER_ERROR.getCode()
                )
            );
            result.setResult(responseEntity);
        } else {
            log.warn("트랜잭션 {}에 대한 DeferredResult를 찾을 수 없거나 이미 완료됨", transactionId);
        }
    }
}
