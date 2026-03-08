package org.ever._4ever_be_alarm.common.async;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@Component
public class GenericAsyncResultManager<T> implements AsyncResultManager<T> {

    private final Map<String, DeferredResult<ResponseEntity<?>>> pendingResults =
        new ConcurrentHashMap<>();

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
            HttpStatus httpStatus = status != null ? status : HttpStatus.OK;
            if (data == null) {
                if (httpStatus == HttpStatus.CREATED) {
                    result.setResult(ResponseEntity.status(httpStatus).build());
                } else {
                    result.setResult(ResponseEntity.noContent().build());
                }
            } else {
                result.setResult(ResponseEntity.status(httpStatus).body(data));
            }
        } else {
            log.warn("트랜잭션 {}에 대한 DeferredResult를 찾을 수 없거나 이미 완료됨", transactionId);
        }
    }

    @Override
    public void setErrorResult(String transactionId, String errorMessage, HttpStatus status) {
        DeferredResult<ResponseEntity<?>> result = pendingResults.get(transactionId);
        if (result != null && !result.isSetOrExpired()) {
            log.info("트랜잭션 {} 오류 결과 설정: {}", transactionId, errorMessage);
            HttpStatus httpStatus = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, errorMessage);
            problemDetail.setTitle(errorMessage);
            problemDetail.setType(URI.create("https://ever.dev/problems/" + httpStatus.value()));
            problemDetail.setProperty("traceId", UUID.randomUUID().toString());
            result.setResult(ResponseEntity.status(httpStatus).body(problemDetail));
        } else {
            log.warn("트랜잭션 {}에 대한 DeferredResult를 찾을 수 없거나 이미 완료됨", transactionId);
        }
    }

    @Override
    public boolean hasPendingResult(String transactionId) {
        DeferredResult<ResponseEntity<?>> result = pendingResults.get(transactionId);
        return result != null && !result.isSetOrExpired();
    }
}
