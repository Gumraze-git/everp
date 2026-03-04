package org.ever._4ever_be_auth.auth.client.service;

public interface ClientValidationService {
    void validateClient(String clientId, String redirectUri);
}
