package org.k5va.controller;

import lombok.RequiredArgsConstructor;
import org.k5va.service.EmployeeCvService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class EmployeeCvController {

    private final EmployeeCvService employeeCvService;

    @ModelAttribute("principal")
    public Principal principal(Principal principal) {
        return principal;
    }

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/cv/{employeeId}")
    public String getEmployeeCv(@PathVariable Long employeeId, Model model) {
        model.addAttribute("cv",
                employeeCvService.getEmployeeCv(employeeId));
        return "cv";
    }

    @GetMapping("/employees")
    public String getAllEmployees(Model model) {
        model.addAttribute("employees",
                employeeCvService.getAllEmployees());
        return "employees";
    }
}