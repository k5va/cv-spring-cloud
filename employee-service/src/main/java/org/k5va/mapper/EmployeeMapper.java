package org.k5va.mapper;

import org.k5va.dto.EmployeeDto;
import org.k5va.generated.tables.records.EmployeesRecord;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeesRecord toRecord(EmployeeDto employeeDto) {
        return new EmployeesRecord(
                employeeDto.id(),
                employeeDto.firstName(),
                employeeDto.lastName(),
                employeeDto.age()
        );
    }

    public EmployeeDto toDto(EmployeesRecord record) {
        return new EmployeeDto(
                record.getId(),
                record.getFirstName(),
                record.getLastName(),
                record.getAge()
        );
    }
}
