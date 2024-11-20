package org.k5va.service;

import lombok.RequiredArgsConstructor;
import org.k5va.client.CvServiceClient;
import org.k5va.dto.CvDto;
import org.k5va.dto.EmployeeDto;
import org.k5va.mapper.EmployeeMapper;
import org.k5va.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final CvServiceClient cvClient;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeDto getById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toDto)
                .orElseThrow();
    }

    public CvDto getEmployeeCv(Long id) {
        return cvClient.getCvById(getById(id).cvId());
    }

    public EmployeeDto create(EmployeeDto employeeDto) {
        var createdEmployee = employeeRepository.create(employeeMapper.toRecord(employeeDto));
        return employeeMapper.toDto(createdEmployee);
    }
}
