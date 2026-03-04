package org.ever._4ever_be_business.hr.integration.port;

import org.ever.event.CreateAuthUserEvent;
import org.springframework.lang.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * 비즈니스 로직에서 외부 User Service와의 의존성을 추상화
 * 실제 구현은 Adapter에서 담당
 */
public interface UserServicePort {
    /**
     * 인증 서비스에 내부 사용자 생성을 요청한다.
     *
     * @param request 생성 이벤트
     * @return 항상 null이 아닌 {@link CompletableFuture}; 즉시 실패를 전달해야 할 경우 {@link CompletableFuture#failedFuture(Throwable)} 사용
     */
    @NonNull
    CompletableFuture<Void> createAuthUserPort(CreateAuthUserEvent request);
}
