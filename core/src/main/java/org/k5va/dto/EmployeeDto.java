package org.k5va.dto;

public record EmployeeDto(
        Long id,
        String firstName,
        String lastName,
        Integer age
) {}
