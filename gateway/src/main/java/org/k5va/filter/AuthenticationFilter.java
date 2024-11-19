package org.k5va.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    public static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/auth/token",
            "/auth/register",
            "eureka"
    );
    private static final String BEARER_AUTH_PREFIX = "Bearer ";

    public static class Config {}

    private final TokenValidator tokenValidator;

    public AuthenticationFilter(TokenValidator tokenValidator) {
        super(Config.class);
        this.tokenValidator = tokenValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (isRouteSecured(request.getURI())) {
                tokenValidator.validate(parseAuthToken(request.getHeaders()));
                log.info("Token is valid");
            }

            return chain.filter(exchange);
        };
    }

    private boolean isRouteSecured(URI route) {
        return OPEN_API_ENDPOINTS.stream()
                .noneMatch(route.getPath()::contains);
    }

    private String parseAuthToken(HttpHeaders headers) {
        if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new RuntimeException("Authorization header is missing");
        }

        String authHeader = headers.get(HttpHeaders.AUTHORIZATION).get(0);
        if (!authHeader.startsWith(BEARER_AUTH_PREFIX)) {
            throw new RuntimeException("Bearer token is missing");
        }

        return authHeader.substring(BEARER_AUTH_PREFIX.length());
    }
}
