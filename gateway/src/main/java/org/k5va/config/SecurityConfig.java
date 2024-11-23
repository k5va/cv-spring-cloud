package org.k5va.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerReactiveAuthenticationManagerResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth.anyExchange().authenticated())
                .oauth2ResourceServer(customizer ->
                        customizer.authenticationManagerResolver(
                                JwtIssuerReactiveAuthenticationManagerResolver
                                        .fromTrustedIssuers(issuerUri)
                        )
                )
                .build();
    }
}
