package org.k5va.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.k5va.client.CvServiceClient;
import org.k5va.dto.CreateEmployeeDto;
import org.k5va.dto.CvDto;
import org.k5va.dto.EmployeeDto;
import org.k5va.dto.OutboxType;
import org.k5va.generated.tables.records.EmployeesRecord;
import org.k5va.generated.tables.records.OutboxRecord;
import org.k5va.mapper.EmployeeMapper;
import org.k5va.repository.EmployeeRepository;
import org.k5va.repository.OutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final CvServiceClient cvClient;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @SneakyThrows
    @Transactional
    public EmployeeDto create(CreateEmployeeDto employeeDto) {
        EmployeesRecord createdEmployee = employeeRepository.create(
                employeeMapper.toRecord(employeeDto));

        CvDto cvDto = employeeMapper.toCvDto(employeeDto, createdEmployee.getId());
        OutboxRecord outboxRecord = new OutboxRecord();
        outboxRecord.setPayload(objectMapper.writeValueAsString(cvDto));
        outboxRecord.setType(OutboxType.CV.name());
        outboxRepository.create(outboxRecord);

        return employeeMapper.toDto(createdEmployee);
    }
}
