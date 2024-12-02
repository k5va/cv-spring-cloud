package org.k5va.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.CreateEmployeeDto;
import org.k5va.dto.CvDto;
import org.k5va.dto.EmployeeDto;
import org.k5va.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        log.info("Get employee {}", id);
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("Get all employees");
        return ResponseEntity.ok(employeeService.getAll());
    }

    @GetMapping("/{id}/cv")
    public ResponseEntity<CvDto> getEmployeeCv(@PathVariable String id) {
        log.info("Get cv for employee {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeCv(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> create(@RequestBody CreateEmployeeDto employeeDto) {
        log.info("Create employee {}", employeeDto);
        EmployeeDto createdEmployee = employeeService.create(employeeDto);

        return ResponseEntity
                .created(UriComponentsBuilder
                        .fromPath("/employee/{id}")
                        .buildAndExpand(createdEmployee.id())
                        .toUri())
                .body(createdEmployee);
    }
}
