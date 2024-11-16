package org.k5va.dto;

/**
 * @author Alexey Kulikov
 */
public record EmployeeDto(
        Long id,
        Long cvId,
        String firstName,
        String lastName,
        Integer age
) {}
