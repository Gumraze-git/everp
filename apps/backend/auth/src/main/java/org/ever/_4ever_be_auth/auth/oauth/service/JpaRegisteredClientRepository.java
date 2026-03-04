package org.ever._4ever_be_auth.auth.oauth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.auth.oauth.entity.RegisteredClientEntity;
import org.ever._4ever_be_auth.auth.oauth.repository.RegisteredClientJpaRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaRegisteredClientRepository implements RegisteredClientRepository {

    private final RegisteredClientJpaRepository jpaRepository;
    private final RegisteredClientMapper mapper;

    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {
        RegisteredClientEntity entity = mapper.toEntity(registeredClient);
        Optional<RegisteredClientEntity> existing = jpaRepository.findById(entity.getId());
        if (existing.isPresent()) {
            RegisteredClientEntity target = existing.get();
            target.updateFrom(entity);
        } else {
            log.info("[INFO] 새로운 등록된 클라이언트(RegisteredClient)를 저장합니다.: {}", entity.getId());
            jpaRepository.save(entity);
        }
    }

    @Override
    public RegisteredClient findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toRegisteredClient)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return jpaRepository.findByClientId(clientId)
                .map(mapper::toRegisteredClient)
                .orElse(null);
    }
}
