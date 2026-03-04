package org.ever._4ever_be_gw.config.webclient;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "api")
public class ApiProperties {

    private Map<String, ClientProperties> clients;

    @Getter
    @Setter
    public static class ClientProperties {

        private String baseUrl;
//        private long connectTimeoutMillis;
//        private long readTimeoutMillis;
    }


}
