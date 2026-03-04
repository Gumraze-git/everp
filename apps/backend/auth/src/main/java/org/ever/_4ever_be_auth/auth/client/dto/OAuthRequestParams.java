package org.ever._4ever_be_auth.auth.client.dto;

public record OAuthRequestParams(
        String clientId,
        String redirectUri,
        String responseType,
        String scope,
        String state,
        String codeChallenge,
        String codeChallengeMethod
) {
}
