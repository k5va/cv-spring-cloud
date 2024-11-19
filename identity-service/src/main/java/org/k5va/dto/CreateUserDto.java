package org.k5va.dto;

import java.util.List;

/**
 * @author Alexey Kulikov
 */
public record CreateUserDto(
        String username,
        String email,
        String password,
        List<String> roles
) {}
