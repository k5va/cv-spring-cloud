package org.k5va.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Access token for user authentication
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString
public class AccessToken {
    /**
     * Encoded string representation of access token
     */
    private final String token;
    
    /**
     * User name
     */
    private final String userName;

    /**
     * List of user roles
     */
    private final List<String> roles;

    /**
     * Token issue date
     */
    private final Date issuedDate;

    /**
     * Token expiration date
     */
    private final Date expiredDate;
}
