package org.k5va.service;

import lombok.RequiredArgsConstructor;
import org.k5va.client.CvServiceClient;
import org.k5va.dto.CreateEmployeeDto;
import org.k5va.dto.CvDto;
import org.k5va.dto.EmployeeDto;
import org.k5va.mapper.EmployeeMapper;
import org.k5va.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.k5va.producer.CvProducer;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final CvServiceClient cvClient;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final CvProducer cvProducer;

    public EmployeeDto getById(Long id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public List<EmployeeDto> getAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    public CvDto getEmployeeCv(String id) {
        return cvClient.getCvByEmployeeId(id);
    }

    public EmployeeDto create(CreateEmployeeDto employeeDto) {
        var createdEmployee = employeeRepository.create(employeeMapper
                .toRecord(new EmployeeDto(null,
                        employeeDto.firstName(),
                        employeeDto.lastName(),
                        employeeDto.age())));

        cvProducer.sendCreateCvEvent(createdEmployee.getId(), employeeDto);

        return employeeMapper.toDto(createdEmployee);
    }
}
