package org.k5va.mapper;

import org.k5va.dto.CreateEmployeeDto;
import org.k5va.dto.CvDto;
import org.k5va.dto.EmployeeDto;
import org.k5va.generated.tables.records.EmployeesRecord;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeesRecord toRecord(CreateEmployeeDto employeeDto) {
        return new EmployeesRecord(
                null,
                employeeDto.firstName(),
                employeeDto.lastName(),
                employeeDto.age()
        );
    }

    public CvDto toCvDto(CreateEmployeeDto employeeDto, Long employeeId) {
        return new CvDto(
                null,
                employeeDto.education(),
                employeeDto.description(),
                employeeDto.workExperience(),
                employeeDto.skills(),
                employeeDto.languages(),
                employeeDto.certificates(),
                employeeDto.linkedId(),
                employeeDto.isOpenToWork(),
                employeeId
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
