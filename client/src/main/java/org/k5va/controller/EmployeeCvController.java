package org.k5va.controller;

import lombok.RequiredArgsConstructor;
import org.k5va.service.EmployeeCvService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

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
    public String getEmployeeCv(Model model) {
        model.addAttribute("cv",
                employeeCvService.getEmployeeCv(1L));
        return "cv";
    }
}