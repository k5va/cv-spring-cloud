package org.k5va.service;

import lombok.RequiredArgsConstructor;
import org.k5va.dto.CvDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeCvService {
    private final RestClient cvRestClient;

    public CvDto getEmployeeCv(Long employeeId) {
        return Optional.ofNullable(cvRestClient.get()
                .uri("/employee/{employeeId}/cv", employeeId)
                .retrieve()
                .body(CvDto.class))
                .orElseThrow(() -> new RuntimeException("Cv not found"));
    }
}
