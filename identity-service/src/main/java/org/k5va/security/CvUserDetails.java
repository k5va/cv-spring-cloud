package org.k5va.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CvUserDetails extends User {
    private final String email;

    public CvUserDetails(String username,
                         String email,
                         String password,
                         Collection<? extends GrantedAuthority> authorities) {

        super(username, password, authorities);
        this.email = email;
    }
}
