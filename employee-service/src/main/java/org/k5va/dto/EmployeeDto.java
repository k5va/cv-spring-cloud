package org.k5va.dto;

public record EmployeeDto(
        Long id,
        Long cvId,
        String firstName,
        String lastName,
        Integer age
) {}
