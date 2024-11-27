package org.k5va.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerReactiveAuthenticationManagerResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("#{'${spring.security.oauth2.resourceserver.jwt.issuer-uri}'.split(',')}")
    private List<String> issuerUris;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(this::customizeResourceServer)
                .build();
    }

    private void customizeResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec customizer) {
        customizer.authenticationManagerResolver(JwtIssuerReactiveAuthenticationManagerResolver
                .fromTrustedIssuers(issuerUris)
        );
    }
}
