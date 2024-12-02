package org.k5va.events;

import java.util.List;

public record CreateCvEvent(
        Long employeeId,
        String education,
        String description,
        String workExperience,
        List<String> skills,
        List<String> languages,
        List<String> certificates,
        String linkedId,
        boolean isOpenToWork
) {
}
