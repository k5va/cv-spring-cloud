package org.k5va.dto;

/**
 * @author Alexey Kulikov
 */
public record AuthRequestDto(
        String username,
        String password) {}
