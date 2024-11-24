package org.k5va.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/employees/**", "/cv/**").hasAuthority("USER")
                        .anyRequest().authenticated())
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .logout(customizer -> customizer
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/"))
                .build();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) ->
                authorities.stream()
                        .filter(authority -> authority instanceof OidcUserAuthority)
                        .map(OidcUserAuthority.class::cast)
                        .flatMap(oidcUserAuthority -> oidcUserAuthority
                                .getUserInfo().getClaimAsStringList("groups")
                                .stream()
                                .map(SimpleGrantedAuthority::new)
                        )
                        .toList();
    }
}
