package com.pulseboard.core.adapter.in.web.security;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Secrets come from the environment; empty values mean "nothing is authorised" (fail closed). */
@ConfigurationProperties(prefix = "pulseboard.security")
public record SecurityProperties(List<String> apiKeys, List<String> adminKeys, String webhookSecret) {

    public SecurityProperties {
        apiKeys = apiKeys == null ? List.of() : List.copyOf(apiKeys);
        adminKeys = adminKeys == null ? List.of() : List.copyOf(adminKeys);
        webhookSecret = webhookSecret == null ? "" : webhookSecret;
    }
}
