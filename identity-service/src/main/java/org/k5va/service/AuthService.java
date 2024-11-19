package org.k5va.service;

import lombok.RequiredArgsConstructor;
import org.k5va.dto.AuthRequestDto;
import org.k5va.security.AccessToken;
import org.k5va.security.AccessTokenFactory;
import org.k5va.security.CvUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexey Kulikov
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AccessTokenFactory accessTokenFactory;

    public AccessToken getToken(AuthRequestDto request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

//        CvUserDetails userDetails = this.usersService.loadUserByUsername(authenticate.getName());
        List<String> roles = authenticate.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return accessTokenFactory.createToken(authenticate.getName(), roles);
    }
}
