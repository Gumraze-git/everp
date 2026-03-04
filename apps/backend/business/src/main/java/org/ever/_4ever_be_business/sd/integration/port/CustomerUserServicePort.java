package org.ever._4ever_be_business.sd.integration.port;

import java.util.concurrent.CompletableFuture;
import org.ever.event.CreateCustomerUserEvent;
import org.springframework.lang.NonNull;

/**
 * 고객사 사용자 계정 생성을 외부(Auth) 서비스에 요청하기 위한 포트.
 */
public interface CustomerUserServicePort {

    @NonNull
    CompletableFuture<Void> createCustomerUser(CreateCustomerUserEvent event);
}
