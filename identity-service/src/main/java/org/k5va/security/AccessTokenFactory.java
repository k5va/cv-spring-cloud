package org.k5va.security;

import java.util.List;

/**
 * Factory for creating access tokens
 * @see AccessToken
 */
public interface AccessTokenFactory {

    /**
     * Creates new access token
     * @param userName user name
     * @param roles List of user roles
     * @return access token
     */
    AccessToken createToken(String userName, List<String> roles);

    /**
     * Parses access token from encoded token string
     * @param token encoded token string
     * @return access token
     */
    AccessToken parseToken(String token);
}
