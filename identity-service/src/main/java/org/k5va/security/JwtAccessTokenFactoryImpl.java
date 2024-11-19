package org.k5va.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Factory for creating JWT access tokens
 * @see AccessTokenFactory
 */
@Component
class JwtAccessTokenFactoryImpl implements AccessTokenFactory {
    /**
     * Token lifetime
     */
    private final Duration lifetime;

    /**
     * Secret key for token verification and signing
     */
    private final SecretKey secretKey;

    /**
     * Creates new Access token factory
     * @param lifetime token lifetime
     * @param secret secret for signing and verifying token
     */
    public JwtAccessTokenFactoryImpl(@Value("${jwt.lifetime}") Duration lifetime,
                                     @Value("${jwt.secret}") String secret) {

        this.lifetime = lifetime;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    @Override
    public AccessToken createToken(String userName, List<String> roles) {
        var issuedDate = new Date();
        var expiredDate = new Date(issuedDate.getTime() + lifetime.toMillis());

        var token = Jwts.builder()
                .claims(Map.of("roles", roles))
                .subject(userName)
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(this.secretKey)
                .compact();

        return new AccessToken(token, userName, roles, issuedDate, expiredDate );
    }

    @Override
    public AccessToken parseToken(String token) {
        var payload = Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        var roles = ((List<?>) payload.get("roles", List.class))
                .stream()
                .map(String::valueOf)
                .toList();

        var userName = payload.getSubject();
        var issuedDate = payload.getIssuedAt();
        var expiredDate = payload.getExpiration();

        return new AccessToken(token, userName, roles, issuedDate, expiredDate);
    }
}