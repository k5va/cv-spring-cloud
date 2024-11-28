package org.k5va.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/employees/**", "/cv/**").hasAuthority("USER")
                        .anyRequest().authenticated())
//                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler()))
                .oidcLogout((logout) -> logout
                        .backChannel(Customizer.withDefaults())
                )
                .build();
    }

    @Bean
    public HttpSessionEventPublisher sessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) ->
                authorities.stream()
                        .filter(authority -> authority instanceof OidcUserAuthority)
                        .map(OidcUserAuthority.class::cast)
                        .flatMap(this::mapOidcUserAuthorityToSimpleGrantedAuthorities)
                        .toList();
    }

    private Stream<SimpleGrantedAuthority> mapOidcUserAuthorityToSimpleGrantedAuthorities(OidcUserAuthority oidcUserAuthority) {
        Map<String, Collection<String>> realmAccess = oidcUserAuthority
                .getIdToken()
                .getClaim("realm_access");

        if (realmAccess == null || realmAccess.get("roles") == null) {
            log.warn("realm_access or roles is null");
            return Stream.empty();
        }

        return realmAccess.get("roles").stream()
                .map(SimpleGrantedAuthority::new);
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);

        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

        return oidcLogoutSuccessHandler;
    }
}
