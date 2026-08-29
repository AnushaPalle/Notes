package com.pulseboard.core.adapter.in.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates /api/** with an API key (header X-API-Key).
 * Two privilege levels: normal keys for ingest and reads, admin keys for /api/admin/**.
 * Webhooks are exempt here because they carry their own HMAC signature.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    static final String HEADER = "X-API-Key";

    private final SecurityProperties properties;

    public ApiKeyFilter(SecurityProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/") || path.startsWith("/api/webhooks/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String key = request.getHeader(HEADER);
        boolean admin = matches(properties.adminKeys(), key);
        boolean normal = matches(properties.apiKeys(), key);

        if (!admin && !normal) {
            reject(response, HttpServletResponse.SC_UNAUTHORIZED, "missing or invalid API key");
        } else if (!admin && request.getRequestURI().startsWith("/api/admin")) {
            reject(response, HttpServletResponse.SC_FORBIDDEN, "admin key required");
        } else {
            chain.doFilter(request, response);
        }
    }

    /** Compares against every configured key in constant time per comparison. */
    static boolean matches(List<String> keys, String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return false;
        }
        byte[] given = candidate.getBytes(StandardCharsets.UTF_8);
        boolean found = false;
        for (String key : keys) {
            if (!key.isBlank()) {
                found |= MessageDigest.isEqual(key.getBytes(StandardCharsets.UTF_8), given);
            }
        }
        return found;
    }

    private static void reject(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
