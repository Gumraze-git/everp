package org.ever._4ever_be_auth.auth.client.serviceImpl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.ever._4ever_be_auth.auth.client.exception.ClientValidationException;
import org.ever._4ever_be_auth.auth.client.service.ClientValidationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Getter
@Setter
@AllArgsConstructor
@Service
public class ClientValidationServiceImpl implements ClientValidationService {

    private final RegisteredClientRepository registeredClientRepository;

    @Override
    public void validateClient(String clientId, String redirectUri) {
        // 클라이언트 id 검사
        if (!StringUtils.hasText(clientId)) {
            throw new ClientValidationException("client_id가 비어 있습니다.");
        }

        // 클라이언트 등록정보 조회
        RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);

        // registeredClient 검사
        if (registeredClient == null) {
            throw new ClientValidationException("등록되지 않은 client_id 입니다.");
        }

        // redirect_uri 검사
        if (StringUtils.hasText(redirectUri) && !registeredClient.getRedirectUris().contains(redirectUri)) {
            throw new ClientValidationException("허용되지 않은 redirect_uri입니다.");
        }
    }
}
