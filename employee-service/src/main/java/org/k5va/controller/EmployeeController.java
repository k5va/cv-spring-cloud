package org.k5va.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.CvDto;
import org.k5va.dto.EmployeeDto;
import org.k5va.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexey Kulikov
 */
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        log.info("Get employee {}", id);
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    @GetMapping("/{id}/cv")
    public ResponseEntity<CvDto> getEmployeeCv(@PathVariable Long id) {
        log.info("Get cv for employee {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeCv(id));
    }
}
