package org.k5va.service;

import lombok.RequiredArgsConstructor;
import org.k5va.client.CvServiceClient;
import org.k5va.dto.CvDto;
import org.k5va.dto.EmployeeDto;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final CvServiceClient cvClient;

    public EmployeeDto getEmployee(Long id) {
        return Optional.of(getAllEmployees()
                .get(id))
                .orElseThrow();
    }

    public CvDto getEmployeeCv(Long id) {
        return cvClient.getCvById(getEmployee(id).cvId());
    }

    private Map<Long, EmployeeDto> getAllEmployees() {
        return Map.of(
                1L, new EmployeeDto(1L, 1L, "Ivan", "Ivanov", 20),
                2L, new EmployeeDto(2L, 2L,"Petr", "Petrov", 25),
                3L, new EmployeeDto(3L, 3L,"Sergey", "Sergeev", 30),
                4L, new EmployeeDto(4L, 4L,"Vladimir", "Vladimirov", 35),
                5L, new EmployeeDto(5L, 5L, "Alexey", "Alexeev", 40),
                6L, new EmployeeDto(6L, 6L,"Mikhail", "Mikhailov", 45),
                7L, new EmployeeDto(7L, 7L,"Nikita", "Nikitov", 50),
                8L, new EmployeeDto(8L, 8L,"Sergey", "Sergeev", 55),
                9L, new EmployeeDto(9L, 9L,"Vladimir", "Vladimirov", 60),
                10L, new EmployeeDto(10L,10L, "Alexey", "Alexeev", 65)
        );
    }
}
