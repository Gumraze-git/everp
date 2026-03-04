package org.ever._4ever_be_gw.config.webclient;

import lombok.Getter;

@Getter
public enum ApiClientKey {
    GATEWAY("gateway"),
    AUTH("auth"),
    ALARM("alarm"),
    PAYMENT("payment"),
    BUSINESS("business"),
    SCM_PP("scm");

    private final String propertyKey;

    ApiClientKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }
}
