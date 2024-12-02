package org.k5va.dto;

import java.util.List;

public record CreateEmployeeDto(
        String firstName,
        String lastName,
        Integer age,
        String education,
        String description,
        String workExperience,
        List<String> skills,
        List<String> languages,
        List<String> certificates,
        String linkedId,
        boolean isOpenToWork
) {}
